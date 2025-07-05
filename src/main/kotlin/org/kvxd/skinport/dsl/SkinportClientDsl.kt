package org.kvxd.skinport.dsl

import org.kvxd.skinport.ClientFlags
import org.kvxd.skinport.SkinportClient
import org.kvxd.skinport.cache.SkinportCache
import org.kvxd.skinport.cache.impl.SkinportFileCache
import java.io.File

@SkinportApiMarker
public annotation class SkinportClientDsl

@SkinportClientDsl
public class SkinportClientBuilder {
    public var flags: ClientFlags = ClientFlags()
    public var cache: SkinportCache? = null

    public fun build(): SkinportClient {
        return SkinportClient(flags, cache)
    }

    public fun fileCache(directory: File = File("skinport-cache"), ttlMillis: Long = 5 * 60 * 1000L) {
        cache = SkinportFileCache(directory, ttlMillis)
    }

    public fun flags(flagsCfg: ClientFlags.() -> Unit) {
        val clientFlags = ClientFlags().apply(flagsCfg)
        flags = clientFlags
    }
}

@SkinportClientDsl
public fun skinportClient(block: SkinportClientBuilder.() -> Unit): SkinportClient {
    return SkinportClientBuilder().apply(block).build()
}