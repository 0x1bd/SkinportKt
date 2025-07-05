package org.kvxd.skinport.cache

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

public interface SkinportCache {
    public suspend fun <T> get(key: String, type: DeserializationStrategy<T>): T?
    public suspend fun <T> put(key: String, value: T, serializer: SerializationStrategy<T>)
}