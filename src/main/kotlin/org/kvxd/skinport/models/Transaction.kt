package org.kvxd.skinport.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionsResponse(
    val pagination: Pagination,
    val data: List<TransactionData>
) {

    @Serializable
    data class Pagination(
        val page: Int,
        val pages: Int,
        val limit: Int,
        val order: Order
    )

    @Serializable
    data class TransactionData(
        val id: Long,
        val type: Type,
        @SerialName("sub_type")
        val subType: SubType? = null,
        val status: Status,
        val amount: Double,
        val fee: Double? = null,
        val currency: Currency,
        val items: List<Item>? = null,
        @SerialName("created_at")
        val createdAt: Instant,
        @SerialName("updated_at")
        val updatedAt: Instant
    ) {

        @Serializable
        data class Item(
            @SerialName("asset_id")
            val assetId: Long,
            @SerialName("sale_id")
            val saleId: Long,
            @SerialName("market_hash_name")
            val marketHashName: String,
            @SerialName("seller_country")
            val sellerCountry: String,
            @SerialName("buyer_country")
            val buyerCountry: String,
            val amount: Double,
            val currency: Currency
        )

        @Serializable
        enum class Type {

            @SerialName("credit")
            CREDIT,

            @SerialName("withdraw")
            WITHDRAW,

            @SerialName("purchase")
            PURCHASE

        }

        @Serializable
        enum class SubType {

            @SerialName("item")
            Item

        }


        @Serializable
        enum class Status {

            @SerialName("complete")
            COMPLETE,

            @SerialName("initiate")
            INITIATE

        }
    }
}
