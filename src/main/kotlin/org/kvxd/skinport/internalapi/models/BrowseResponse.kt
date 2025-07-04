package org.kvxd.skinport.internalapi.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.kvxd.skinport.models.Currency

@Serializable
public data class BrowseResponse(
    val requestId: String,
    val success: Boolean,
    val message: String? = null,
    val filter: Filter,
    val items: List<Listing>
)

@Serializable
public data class Listing(
    val id: Int,
    val saleId: Int,
    val shortId: String,
    val productId: Int,
    val itemId: Int,
    val appid: Int,
    val steamid: String,
    val url: String,
    val family: String,
    val name: String,
    val title: String,
    val text: String,
    val marketName: String,
    val marketHashName: String,
    val color: String,
    val bgColor: String? = null,
    val image: String,
    val lock: Instant? = null,
    val suggestedPrice: Int,
    val salePrice: Int,
    val category: String,
    val subCategory: String,
    val pattern: Int,
    val finish: Int,
    val customName: String? = null,
    val wear: Float,
    val link: String,
    val type: String,
    val stickers: List<Sticker> = emptyList(),
    val souvenir: Boolean,
    val stattrak: Boolean
)

@Serializable
public data class Sticker(
    val img: String, val name: String, val slot: Int
)

@Serializable
public data class Filter(
    val total: Int
)


public enum class Order {

    @SerialName("asc")
    ASCENDING,

    @SerialName("desc")
    DESCENDING

}

public enum class Sort {

    @SerialName("sale")
    VALUE,

    @SerialName("percent")
    DISCOUNT,

    @SerialName("date")
    DATE,

    @SerialName("wear")
    WEAR

}