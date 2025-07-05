package org.kvxd.skinport.pooling

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.kvxd.skinport.SkinportClient

public class SkinportClientPool(
    private val clientFactory: () -> SkinportClient,
    private val maxPerClient: Int = 50,
    private val maxClients: Int = 5
) {

    private val mutex = Mutex()
    private val clients = ArrayDeque<PooledClient>()

    /**
     * Runs the given suspending function with an available SkinportClient from the pool.
     * This method suspends until a client is available.
     */
    public suspend fun <T> withClient(function: suspend SkinportClient.() -> T): T =
        function(getClient())

    /**
     * Retrieves a [SkinportClient] from the pool.
     * Recycles clients until [maxPerClient] usage count is reached, then closes and replaces them.
     */
    public suspend fun getClient(): SkinportClient = mutex.withLock {
        // Remove exhausted or bad clients
        while (clients.isNotEmpty() && (clients.first().isExhausted())) {
            clients.removeFirst().close()
        }

        while (clients.size < maxClients) {
            clients.addLast(PooledClient(clientFactory(), maxPerClient))
        }

        val pooledClient = clients.removeFirst()
        pooledClient.incrementUsage()
        clients.addLast(pooledClient)
        pooledClient.client
    }

    /**
     * Closes all clients in the pool and clears the pool.
     */
    public suspend fun closeAll() {
        mutex.withLock {
            clients.forEach { it.close() }
            clients.clear()
        }
    }
}
