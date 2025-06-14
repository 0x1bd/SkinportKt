package org.kvxd.skinport.dsl

import org.kvxd.skinport.SkinportAPISecret
import org.kvxd.skinport.SkinportClient
import org.kvxd.skinport.cache.SkinportCache
import org.kvxd.skinport.cache.impl.SkinportFileCache
import java.io.File

@SkinportApiMarker
annotation class SkinportClientDsl

@SkinportClientDsl
class SkinportClientBuilder {
    var auth: SkinportAPISecret? = null
    var cache: SkinportCache? = null

    fun build(): SkinportClient {
        return SkinportClient(auth, cache)
    }

    fun fileCache(directory: File = File("skinport-cache"), ttlMillis: Long = 5 * 60 * 1000L) {
        cache = SkinportFileCache(directory, ttlMillis)
    }
}

@SkinportClientDsl
fun skinportClient(block: SkinportClientBuilder.() -> Unit): SkinportClient {
    return SkinportClientBuilder().apply(block).build()
}