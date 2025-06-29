package org.kvxd.skinport.internalapi.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BrowseResponse(
    val requestId: String,
    val success: Boolean,
    val message: String? = null,
    val filter: Filter,
    val items: List<Listing>
)

@Serializable
data class Listing(
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
data class Sticker(
    val img: String,
    val name: String,
    val slot: Int
)

@Serializable
data class Filter(
    val total: Int
)