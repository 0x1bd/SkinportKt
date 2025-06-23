package org.kvxd.skinport.internalapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import org.jetbrains.annotations.Range
import org.kvxd.skinport.internalapi.models.BrowseResponse
import org.kvxd.skinport.ProxyCfg
import org.kvxd.skinport.defaultHttpClient

@OptIn(InternalSkinportAPI::class)
class InternalSkinportClient(
    private val proxyCfg: ProxyCfg? = null,

    private val client: HttpClient = defaultHttpClient(
        proxyCfg = proxyCfg,
        mimicBrowser = true,
    )
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
        }
            .also {
                println(it.bodyAsText())
            }
            .body()

}

private fun Boolean.toInt(): Int = when (this) {
    true -> 1
    false -> 0
}