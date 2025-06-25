package org.kvxd.skinport

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.builtins.ListSerializer
import org.jetbrains.annotations.Range
import org.kvxd.skinport.cache.SkinportCache
import org.kvxd.skinport.models.*

private const val BASE_ENDPOINT = "https://api.skinport.com/v1/"

class SkinportClient(
    internal val flags: ClientFlags,
    internal val cache: SkinportCache?,
    internal val client: HttpClient = defaultHttpClient(flags)
) {

    /**
     * Retrieves data for a given item with pricing data from the last 24 hours.
     */
    suspend fun items(
        appId: Int? = null,
        tradable: Boolean? = null
    ): Result<List<Item>> = runCatching {
        val cacheKey = buildString {
            append("items")
            if (appId != null) append(":appId=$appId")
            if (tradable != null) append(":tradable=$tradable")
        }

        cache?.get(cacheKey, ListSerializer(Item.serializer()))?.let {
            return@runCatching it
        }

        val response = client.get(BASE_ENDPOINT + "items") {
            if (appId != null)
                parameter("app_id", appId)
            if (tradable != null)
                parameter("tradable", tradable)
        }

        val result: List<Item> = response.body()
        cache?.put(cacheKey, result, ListSerializer(Item.serializer()))
        return@runCatching result
    }

    /**
     * Retrieves the sales history for a given list of items (seperated by commas)
     */
    suspend fun salesHistory(
        marketHashName: String? = null,
        appId: Int? = null,
    ): Result<List<SalesHistoryItem>> = runCatching {
        val cacheKey = buildString {
            append("salesHistory")
            if (marketHashName != null) append(":$marketHashName")
            if (appId != null) append(":$appId")
        }

        cache?.get(cacheKey, ListSerializer(SalesHistoryItem.serializer()))?.let {
            return@runCatching it
        }

        val response = client.get(BASE_ENDPOINT + "sales/history") {
            if (marketHashName != null)
                parameter("market_hash_name", marketHashName)
            if (appId != null)
                parameter("app_id", appId)
        }

        val result: List<SalesHistoryItem> = response.body()
        cache?.put(cacheKey, result, ListSerializer(SalesHistoryItem.serializer()))
        return@runCatching result
    }

    /**
     * Retrieves all items that are out-of-stock on skinport with limited pricing data from the last 90 days.
     */
    suspend fun outOfStockItems(appId: Int? = null): Result<List<OutOfStockItem>> = runCatching {
        val cacheKey = if (appId != null) "outOfStock:$appId" else "outOfStock"

        cache?.get(cacheKey, ListSerializer(OutOfStockItem.serializer()))?.let {
            return@runCatching it
        }

        val response = client.get(BASE_ENDPOINT + "sales/out-of-stock") {
            if (appId != null)
                parameter("app_id", appId)
        }

        val data: List<OutOfStockItem> = response.body()
        cache?.put(cacheKey, data, ListSerializer(OutOfStockItem.serializer()))
        return@runCatching data
    }


    /**
     * Retrieves a paginated list of all user account transactions.
     */
    suspend fun transactions(
        page: Int = 1,
        limit: @Range(from = 1, to = 100) Int = 100,
        order: Order = Order.DESC
    ): Result<TransactionsResponse> = runCatching {
        require(flags.apiSecret != null) { "Transactions endpoint requires authentication" }

        val response = client.get(BASE_ENDPOINT + "account/transactions") {
            parameter("page", page)
            parameter("limit", limit)
            parameter("order", order.name.lowercase())
        }

        return@runCatching response.body()
    }

    fun close() {
        client.close()
    }

}