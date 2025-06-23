package org.kvxd.skinport.internalapi

import org.kvxd.skinport.ProxyCfg
import org.kvxd.skinport.dsl.SkinportApiMarker

@SkinportApiMarker
annotation class InternalSkinportClientDsl

@InternalSkinportClientDsl
class InternalSkinportClientBuilder {

    var proxyCfg: ProxyCfg? = null

    fun build(): InternalSkinportClient {
        return InternalSkinportClient(proxyCfg = proxyCfg)
    }

    fun proxy(proxy: ProxyCfg) {
        proxyCfg = proxy
    }
}

@InternalSkinportClientDsl
@InternalSkinportAPI
fun internalSkinportClient(block: InternalSkinportClientBuilder.() -> Unit): InternalSkinportClient {
    return InternalSkinportClientBuilder().apply(block).build()
}