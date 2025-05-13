package io.github.kvxd.skinportkt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single item listed on Skinport's marketplace.
 *
 * Includes market data such as pricing, quantity, and timestamp information.
 * Some price-related fields may be `null` if incomplete data is returned by the API.
 *
 **/
@Serializable
data class SkinportMarketListing(
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
    val minimumPrice: Double? = null,
    @SerialName("max_price")
    val maximumPrice: Double? = null,
    @SerialName("mean_price")
    val meanPrice: Double? = null,
    @SerialName("median_price")
    val medianPrice: Double? = null,
    val quantity: Int,
    @SerialName("created_at")
    val createdTimestamp: Long,
    @SerialName("updated_at")
    val updatedTimestamp: Long
) {

    /**
     * Indicates whether the item has complete pricing data.
     *
     * A "complete" item is one where all relevant price fields are non-null.
     */
    val completePricingData: Boolean
        get() = listOf(suggestedPrice, minimumPrice, maximumPrice, meanPrice, medianPrice).all { it != null }

}