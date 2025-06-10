package org.kvxd.skinport

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import okhttp3.OkHttpClient
import okhttp3.brotli.BrotliInterceptor
import org.jetbrains.annotations.Range
import org.kvxd.skinport.models.*

private const val BASE_ENDPOINT = "https://api.skinport.com/v1/"

class SkinportClient(
    private val apiSecret: SkinportAPISecret
) {

    private val client: HttpClient = HttpClient(OkHttp) {
        engine {
            preconfigured = OkHttpClient.Builder()
                .addInterceptor(BrotliInterceptor)
                .build()
        }

        install(ContentNegotiation) {
            json()
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 1000 * 25
        }

        install(createErrorPlugin())

        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(apiSecret.clientId, apiSecret.clientSecret)
                }
                sendWithoutRequest { request ->
                    request.url.encodedPath.contains("/account/")
                }
            }
        }
    }

    /**
     * Retrieves data for a given item with pricing data from the last 24 hours.
     */
    suspend fun items(
        appId: Int = 730,
        currency: Currency = Currency.EUR,
        tradable: Boolean = false
    ): List<Item> {
        val response = client.get(BASE_ENDPOINT + "items") {
            parameter("app_id", appId)
            parameter("currency", currency)
            parameter("tradable", tradable)
        }

        return response.body()
    }

    /**
     * Retrieves the sales history for a given list of items (seperated by commas)
     */
    suspend fun salesHistory(
        marketHashName: String? = null,
        appId: Int = 730,
        currency: Currency = Currency.EUR
    ): List<SalesHistoryItem> {
        val response = client.get(BASE_ENDPOINT + "sales/history") {
            if (marketHashName != null)
                parameter("market_hash_name", marketHashName)
            parameter("app_id", appId)
            parameter("currency", currency)
        }

        return response.body()
    }

    /**
     * Retrieves all items that are out-of-stock on skinport with limited pricing data from the last 90 days.
     */
    suspend fun outOfSalesItems(
        appId: Int = 730,
        currency: Currency = Currency.EUR
    ): List<OutOfStockItem> {
        val response = client.get(BASE_ENDPOINT + "sales/out-of-stock") {
            parameter("app_id", appId)
            parameter("currency", currency)
        }

        return response.body()
    }

    /**
     * Retrieves a paginated list of all user account transactions.
     */
    suspend fun transactions(
        page: Int = 1,
        limit: @Range(from = 1, to = 100) Int = 100,
        order: Order = Order.DESC
    ): TransactionsResponse {
        val response = client.get(BASE_ENDPOINT + "account/transactions") {
            parameter("page", page)
            parameter("limit", limit)
            parameter("order", order.name.lowercase())
        }

        return response.body()
    }

}