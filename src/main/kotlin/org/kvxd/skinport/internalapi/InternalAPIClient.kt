package org.kvxd.skinport.internalapi

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import org.jetbrains.annotations.Range
import org.kvxd.skinport.SkinportClient
import org.kvxd.skinport.internalapi.models.BrowseResponse

/**
 * Retrieves detailed listings for a given item and skin.
 */
@OptIn(InternalSkinportAPI::class)
suspend fun SkinportClient.browse(
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

private fun Boolean.toInt(): Int = when (this) {
    true -> 1
    false -> 0
}

enum class Order {

    @SerialName("asc")
    ASCENDING,

    @SerialName("desc")
    DESCENDING

}

enum class Sort {

    @SerialName("sale")
    VALUE,

    @SerialName("percent")
    DISCOUNT,

    @SerialName("date")
    DATE,

    @SerialName("wear")
    WEAR

}