package org.kvxd.skinport.internalapi

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.annotations.Range
import org.kvxd.skinport.SkinportClient
import org.kvxd.skinport.internalapi.models.BrowseResponse
import org.kvxd.skinport.internalapi.models.DataResponse
import org.kvxd.skinport.internalapi.models.Order
import org.kvxd.skinport.internalapi.models.Sort

/**
 * Retrieves detailed listings for a given item and skin.
 */
@InternalSkinportAPI
public suspend fun SkinportClient.browse(
    appid: Int = 730,
    item: String,
    skin: String,
    minPrice: Double? = null,
    maxPrice: Double? = null,
    minWear: Float? = null,
    maxWear: Float? = null,
    stattrak: Boolean? = null,
    souvenir: Boolean? = null,
    stickers: Boolean? = null,
    charms: Boolean? = null,
    nametag: Boolean? = null,
    vanilla: Boolean? = null,
    daysLocked: Int? = null,
    skip: @Range(from = 0, to = 20) Int? = null,
    sort: Sort? = null,
    order: Order? = null
): Result<BrowseResponse> =
    runCatching {
        if (!this.flags.mimicBrowser) throw IllegalArgumentException("Internal API requires mimicBrowser to be true.")

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
                if (stickers != null)
                    parameter("stickers", stickers.toInt())
                if (charms != null)
                    parameter("charms", charms.toInt())
                if (nametag != null)
                    parameter("nametag", nametag.toInt())
                if (vanilla != null)
                    parameter("vanilla", vanilla.toInt())

                if (daysLocked != null)
                    parameter("lock", daysLocked)

                if (skip != null)
                    parameter("skip", skip)

                if (sort != null)
                    parameter("sort", sort)
                if (order != null)
                    parameter("order", order)
            }
        }
            .body<BrowseResponse>()
    }

/**
 * Retrieves detailed metadata.
 * Used by [org.kvxd.skinport.pooling.SkinportClientPool] to account for currency differences caused by different proxy locations.
 */
@InternalSkinportAPI
public suspend fun SkinportClient.data(): Result<DataResponse> = runCatching {
    client.get {
        url {
            protocol = URLProtocol.HTTPS
            host = "skinport.com"
            path("api", "data")
        }
    }.body<DataResponse>()
}

private fun Boolean.toInt(): Int = when (this) {
    true -> 1
    false -> 0
}
