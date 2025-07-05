package org.kvxd.skinport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class OutOfStockItem(
    @SerialName("market_hash_name")
    val marketHashName: String,
    val version: String? = null,
    val currency: Currency,
    @SerialName("suggested_price")
    val suggestedPrice: Double? = null,
    @SerialName("avg_sale_price")
    val averageSalePrice: Double? = null,
    @SerialName("sales_last_90d")
    val volumeLast90Days: Int
)