package org.kvxd.skinport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Item(
    @SerialName("market_hash_name")
    val marketHashName: String,
    val currency: String,
    @SerialName("suggested_price")
    val suggestedPrice: Double? = null,
    @SerialName("item_page")
    val itemPage: String,
    @SerialName("market_page")
    val marketPage: String,
    @SerialName("min_price")
    val minPrice: Double? = null,
    @SerialName("max_price")
    val maxPrice: Double? = null,
    @SerialName("mean_price")
    val meanPrice: Double? = null,
    @SerialName("median_price")
    val medianPrice: Double? = null,
    val quantity: Int,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
)