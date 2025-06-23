package org.kvxd.skinport

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.brotli.BrotliInterceptor
import java.net.Proxy

fun defaultHttpClient(
    proxyCfg: ProxyCfg? = null,
    mimicBrowser: Boolean = false,
    apiSecret: SkinportAPISecret? = null,
    useErrorPlugin: Boolean = false,
    enableBrotli: Boolean = false
): HttpClient =
    HttpClient(OkHttp) {

        engine {
            preconfigured = OkHttpClient.Builder().apply {
                if (proxyCfg != null) {
                    proxy(Proxy(Proxy.Type.HTTP, java.net.InetSocketAddress(proxyCfg.host, proxyCfg.port)))

                    if (proxyCfg.username != null && proxyCfg.password != null) {
                        proxyAuthenticator { _, response ->
                            val credentials = Credentials.basic(proxyCfg.username, proxyCfg.password)

                            response.request.newBuilder()
                                .header("Proxy-Authorization", credentials)
                                .build()
                        }
                    }
                }

                if (enableBrotli)
                    addInterceptor(BrotliInterceptor)
            }.build()
        }

        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 25_000
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }

        install(Auth) {
            if (apiSecret != null) {
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

        if (useErrorPlugin)
            install(createErrorPlugin())

        defaultRequest {
            if (mimicBrowser) {
                header(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36"
                )
                header(HttpHeaders.Referrer, "https://skinport.com/")
                header(HttpHeaders.Cookie, "_csrf=abc;")
            }
        }
    }
