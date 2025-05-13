package io.github.kvxd.skinportkt

import io.github.kvxd.skinportkt.util.PersistentCache
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.brotli.BrotliInterceptor
import java.io.IOException
import kotlin.time.Duration.Companion.minutes

class SkinportAPIClient(
    private val secret: SkinportAPISecret? = null,
    cacheDir: String = "cache/skinport"
) {
    private val client = OkHttpClient.Builder()
        .addInterceptor(BrotliInterceptor)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val cache = PersistentCache(
        serializer = ListSerializer(SkinportMarketListing.serializer()),
        cacheDir = cacheDir,
        ttl = 5.minutes
    )

    fun requestItems(
        appId: Int = 730,
        currency: String = "EUR",
        tradable: Boolean = false
    ): List<SkinportMarketListing> {
        val cacheKey = "$appId-$currency-$tradable"

        return cache.getOrPut(cacheKey) {
            fetchFromApi(appId, currency, tradable)
        }
    }

    private fun fetchFromApi(
        appId: Int,
        currency: String,
        tradable: Boolean
    ): List<SkinportMarketListing> {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("api.skinport.com")
            .addPathSegment("v1")
            .addPathSegment("items")
            .addQueryParameter("app_id", appId.toString())
            .addQueryParameter("currency", currency)
            .addQueryParameter("tradable", tradable.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Skinport API request failed with status code ${response.code}: ${response.message}")
        }

        val body = response.body ?: throw IOException("Skinport API response body was null.")

        return json.decodeFromString(body.string())
    }

    fun clearCache() {
        cache.invalidateAll()
    }

    fun clearCache(appId: Int, currency: String, tradable: Boolean) {
        cache.invalidate("$appId-$currency-$tradable")
    }
}