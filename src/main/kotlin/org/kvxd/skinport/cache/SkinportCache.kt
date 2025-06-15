package org.kvxd.skinport.cache

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

interface SkinportCache {
    suspend fun <T> get(key: String, type: DeserializationStrategy<T>): T?
    suspend fun <T> put(key: String, value: T, serializer: SerializationStrategy<T>)
}