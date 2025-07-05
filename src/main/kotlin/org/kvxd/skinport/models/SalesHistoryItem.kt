package org.kvxd.skinport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SalesHistoryItem(
    @SerialName("market_hash_name")
    val marketHashName: String,
    val version: String? = null,
    val currency: Currency,
    @SerialName("item_page")
    val itemPage: String,
    @SerialName("market_page")
    val marketPage: String,
    @SerialName("last_24_hours")
    val last24Hours: HistoricPriceData,
    @SerialName("last_7_days")
    val last7Days: HistoricPriceData,
    @SerialName("last_30_days")
    val last30Days: HistoricPriceData,
    @SerialName("last_90_days")
    val last90Days: HistoricPriceData,
) {

    @Serializable
    public data class HistoricPriceData(
        val min: Double? = null,
        val max: Double? = null,
        @SerialName("avg")
        val average: Double? = null,
        val median: Double? = null,
        val volume: Int
    )

}