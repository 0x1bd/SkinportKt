package io.github.kvxd.skinportkt.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.time.Duration

@Serializable
private data class CacheEntry<T>(
    val data: T,
    val timestamp: Long,
    val expiresAt: Long
)

class PersistentCache<T>(
    private val serializer: KSerializer<T>,
    private val cacheDir: String,
    private val ttl: Duration,
    private val json: Json = Json { 
        prettyPrint = true 
        ignoreUnknownKeys = true 
    }
) {
    init {
        Path(cacheDir).createDirectories()
    }

    /**
     * Gets value from cache if it exists and is valid, otherwise computes and caches new value
     *
     * @param key Cache key
     * @param compute Function to compute new value if cache miss or expired
     * @return Cached or computed value
     */
    fun getOrPut(key: String, compute: () -> T): T {
        val cached = get(key)
        if (cached != null) {
            return cached
        }

        return compute().also { put(key, it) }
    }

    /**
     * Gets value from cache if it exists and is valid
     *
     * @param key Cache key
     * @return Cached value or null if missing or expired
     */
    fun get(key: String): T? {
        val file = getCacheFile(key)
        if (!file.exists()) {
            return null
        }

        return try {
            val entry = json.decodeFromString<CacheEntry<T>>(
                string = file.readText(),
                deserializer = CacheEntry.serializer(serializer)
            )

            if (Instant.now().epochSecond >= entry.expiresAt) {
                file.delete()
                null
            } else {
                entry.data
            }
        } catch (e: Exception) {
            System.err.println("Error reading cache for key '$key': ${e.message}")
            file.delete()
            null
        }
    }

    /**
     * Puts value in cache
     *
     * @param key Cache key
     * @param value Value to cache
     */
    fun put(key: String, value: T) {
        try {
            val now = Instant.now().epochSecond
            val entry = CacheEntry(
                data = value,
                timestamp = now,
                expiresAt = now + ttl.inWholeSeconds
            )

            getCacheFile(key).writeText(
                json.encodeToString(
                    value = entry,
                    serializer = CacheEntry.serializer(serializer)
                )
            )
        } catch (e: Exception) {
            System.err.println("Error writing cache for key '$key': ${e.message}")
        }
    }

    /**
     * Removes specific cache entry
     *
     * @param key Cache key to remove
     */
    fun invalidate(key: String) {
        getCacheFile(key).delete()
    }

    /**
     * Removes all cache entries
     */
    fun invalidateAll() {
        File(cacheDir).listFiles()?.forEach { it.delete() }
    }

    private fun getCacheFile(key: String): File {
        val sanitizedKey = key.replace(Regex("[^a-zA-Z0-9-_]"), "_")
        return File(cacheDir, "$sanitizedKey.json")
    }
}