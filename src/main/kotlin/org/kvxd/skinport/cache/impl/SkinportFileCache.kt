package org.kvxd.skinport.cache.impl

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.kvxd.skinport.cache.SkinportCache
import java.io.File

public class SkinportFileCache(
    private val directory: File,
    private val ttlMillis: Long
) : SkinportCache {

    init {
        directory.mkdirs()
    }

    override suspend fun <T> get(key: String, type: DeserializationStrategy<T>): T? {
        val file = File(directory, "$key.json")
        if (!file.exists() || System.currentTimeMillis() - file.lastModified() > ttlMillis)
            return null

        val json = file.readText()
        return Json.decodeFromString(type, json)
    }

    override suspend fun <T> put(key: String, value: T, serializer: SerializationStrategy<T>) {
        val file = File(directory, "$key.json")
        val json = Json.encodeToString(serializer, value)
        file.writeText(json)
    }
}
