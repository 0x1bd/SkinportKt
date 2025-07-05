package org.kvxd.skinport

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.brotli.BrotliInterceptor
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.security.SecureRandom
import java.util.*

private val USER_AGENTS = listOf(
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/124.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:125.0) Gecko/20100101 Firefox/125.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Edge/124.0.2478.67 Safari/537.36"
)

internal fun configureClientEngine(builder: OkHttpClient.Builder, flags: ClientFlags) {
    flags.proxy?.let { proxyCfg ->
        val host = proxyCfg.host
        val port = proxyCfg.port
        if (host != null && port != null) {
            builder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(host, port)))

            val username = proxyCfg.username
            val password = proxyCfg.password
            if (username != null && password != null) {
                builder.proxyAuthenticator { _, response ->
                    val credentials = Credentials.basic(username, password)

                    response.request.newBuilder()
                        .header("Proxy-Authorization", credentials)
                        .build()
                }
            }
        }
    }

    if (flags.enableBrotli) {
        builder.addInterceptor(BrotliInterceptor)
    }
}

internal fun HttpClientConfig<OkHttpConfig>.configureClientPlugins(flags: ClientFlags) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 25_000
    }

    install(HttpRequestRetry) {
        maxRetries = 3
        retryOnExceptionIf { _, cause ->
            cause is IOException ||
                    cause is SerializationException ||
                    cause is ClientRequestException ||
                    cause is ServerResponseException ||
                    cause is ResponseException
        }
    }

    install(Auth) {
        flags.apiSecret?.let { secret ->
            basic {
                credentials {
                    BasicAuthCredentials(secret.clientId, secret.clientSecret)
                }
                sendWithoutRequest { request ->
                    request.url.encodedPath.contains("/account/")
                }
            }
        }
    }

    defaultRequest {
        if (flags.mimicBrowser) {
            header(
                HttpHeaders.UserAgent,
                USER_AGENTS.random()
            )
            header(HttpHeaders.Referrer, "https://skinport.com/")
            header(HttpHeaders.Cookie, "_csrf=${generateCsrfCookie()};")
        }
    }
}

public fun defaultHttpClient(flags: ClientFlags): HttpClient =
    HttpClient(OkHttp) {
        engine {
            preconfigured = OkHttpClient.Builder().also { builder ->
                configureClientEngine(builder, flags)
            }.build()
        }
        configureClientPlugins(flags)
    }

private fun generateCsrfCookie(): String {
    val bytes = ByteArray(16)
    SecureRandom().nextBytes(bytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}