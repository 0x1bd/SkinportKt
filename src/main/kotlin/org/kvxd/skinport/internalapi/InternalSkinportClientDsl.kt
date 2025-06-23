package org.kvxd.skinport.internalapi

import org.kvxd.skinport.dsl.SkinportApiMarker

@SkinportApiMarker
annotation class InternalSkinportClientDsl

@InternalSkinportClientDsl
class InternalSkinportClientBuilder {

    fun build(): InternalSkinportClient {
        return InternalSkinportClient()
    }
}

@InternalSkinportClientDsl
@InternalSkinportAPI
fun internalSkinportClient(block: InternalSkinportClientBuilder.() -> Unit): InternalSkinportClient {
    return InternalSkinportClientBuilder().apply(block).build()
}