package org.kvxd.skinport.dsl

import org.kvxd.skinport.SkinportAPISecret
import org.kvxd.skinport.SkinportClient

@SkinportApiMarker
annotation class SkinportClientDsl

@SkinportClientDsl
class SkinportClientBuilder {
    var auth: SkinportAPISecret? = null

    fun build(): SkinportClient {
        val secret = auth ?: error("API Secret must be provided")
        return SkinportClient(secret)
    }
}

@SkinportClientDsl
fun skinportClient(block: SkinportClientBuilder.() -> Unit): SkinportClient {
    return SkinportClientBuilder().apply(block).build()
}