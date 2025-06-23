package org.kvxd.skinport.internalapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.Range
import org.kvxd.skinport.internalapi.models.BrowseResponse
import kotlin.math.max

private fun defaultHttpClient(): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 1000 * 25
    }

    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 3)
        exponentialDelay()
    }

    defaultRequest {
        header(
            HttpHeaders.UserAgent,
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36"
        )
        header(HttpHeaders.Referrer, "https://skinport.com")
        header(HttpHeaders.Cookie, "_csrf=abc;")
    }
}

@OptIn(InternalSkinportAPI::class)
class InternalSkinportClient(
    private val client: HttpClient = defaultHttpClient()
) {

    suspend fun browse(
        appid: Int = 730,
        item: String,
        skin: String,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minWear: Float? = null,
        maxWear: Float? = null,
        stattrak: Boolean? = null,
        souvenir: Boolean? = null,
        daysLocked: Int? = null,
        skip: @Range(from = 0, to = 20) Int? = null
    ): BrowseResponse =
        client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "skinport.com"
                path("api", "browse", "$appid")
                parameter("item", skin)
                parameter("type", item)

                if (minPrice != null)
                    parameter("pricegt", minPrice * 100)
                if (maxPrice != null)
                    parameter("pricelt", maxPrice * 100)

                if (minWear != null)
                    parameter("weargt", minWear * 100)
                if (maxWear != null)
                    parameter("wearlt", maxWear * 100)

                if (stattrak != null)
                    parameter("stattrak", stattrak.toInt())
                if (souvenir != null)
                    parameter("souvenir", souvenir.toInt())

                if (daysLocked != null)
                    parameter("lock", daysLocked)

                if (skip != null)
                    parameter("skip", skip)
            }.also {
                println(url.buildString())
            }
        }.body()

}

private fun Boolean.toInt(): Int = when (this) {
    true -> 1
    false -> 0
}