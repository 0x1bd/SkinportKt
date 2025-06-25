package org.kvxd.skinport.pooling

import org.kvxd.skinport.SkinportClient
import java.util.concurrent.atomic.AtomicInteger

internal class PooledClient(
    val client: SkinportClient,
    private val maxUsage: Int
) {
    private val usageCount = AtomicInteger(0)

    fun incrementUsage(): Boolean = usageCount.incrementAndGet() <= maxUsage
    fun isExhausted(): Boolean = usageCount.get() >= maxUsage
    fun close() = client.close()
}