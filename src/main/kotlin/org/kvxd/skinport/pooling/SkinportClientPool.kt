package org.kvxd.skinport.pooling

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import org.kvxd.skinport.SkinportClient

/**
 * A coroutine-based pool for managing [SkinportClient] instances.
 *
 * @param clientFactory Factory function to create new clients
 * @param maxPerClient Maximum requests per client before retirement
 * @param maxClients Maximum concurrent clients in the pool
 */
public class SkinportClientPool(
    private val clientFactory: () -> SkinportClient,
    private val maxPerClient: Int = 5,
    private val maxClients: Int = 5
) {
    private val allocationSemaphore = Semaphore(maxClients)
    private val mutex = Mutex()
    private val available = ArrayDeque<PooledClient>()
    private val allClients = mutableSetOf<PooledClient>()
    private var activeCount = 0

    private inner class PooledClient(
        val client: SkinportClient
    ) {
        var uses: Int = 0

        /** Acquires the client and increments usage count */
        fun acquire(): SkinportClient {
            uses++
            return client
        }

        /** Checks if client has reached maximum usage */
        fun isExhausted() = uses >= maxPerClient

        /** Closes the underlying client */
        fun close() = client.close()
    }

    /**
     * Executes [block] with an acquired client.
     *
     * Guarantees client will be released back to the pool after execution.
     */
    public suspend fun <T> withClient(block: suspend (SkinportClient) -> T): T {
        allocationSemaphore.acquire()
        try {
            val client = mutex.withLock { getAvailableClient() }
            try {
                return block(client)
            } finally {
                releaseClient(client)
            }
        } finally {
            allocationSemaphore.release()
        }
    }

    /** Gets existing available client or creates new one */
    private fun getAvailableClient(): SkinportClient {
        val client = (available.removeFirstOrNull() ?: createNewClient()).acquire()
        activeCount++
        return client
    }

    /** Creates new client when pool capacity allows */
    private fun createNewClient(): PooledClient {
        check(allClients.size < maxClients) { "Max clients reached" }
        return PooledClient(clientFactory()).also { allClients.add(it) }
    }


    /** Releases client back to pool or retires if exhausted */
    private suspend fun releaseClient(client: SkinportClient) {
        var closeClient: PooledClient? = null

        mutex.withLock {
            activeCount--
            val pooled = allClients.find { it.client == client }
                ?: throw IllegalStateException("Unknown client")

            when {
                pooled.isExhausted() -> {
                    allClients.remove(pooled)
                    closeClient = pooled
                }

                else -> available.addLast(pooled)
            }
        }

        closeClient?.close()
    }

    /** Closes all clients and clears the pool */
    public suspend fun close() {
        val clientsToClose = mutex.withLock {
            val clients = allClients.toList()
            available.clear()
            allClients.clear()
            activeCount = 0
            clients
        }
        clientsToClose.forEach { it.close() }
    }

    /** Active client count for debugging */
    public suspend fun activeCount(): Int = mutex.withLock { activeCount }
}