package org.kvxd.skinport

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.kvxd.skinport.models.SkinportError
import org.kvxd.skinport.models.SkinportErrorResponse

sealed class SkinportException(
    val statusCode: Int,
    val error: SkinportError
) : Exception("${error.id}: ${error.message}") {

    class AuthenticationError(status: Int, error: SkinportError) : SkinportException(status, error)
    class ValidationError(status: Int, error: SkinportError) : SkinportException(status, error)
    class NotFound(status: Int, error: SkinportError) : SkinportException(status, error)
    class RateLimitExceeded(status: Int, error: SkinportError) : SkinportException(status, error)
    class GenericError(status: Int, error: SkinportError) : SkinportException(status, error)

    companion object {
        fun from(statusCode: Int, error: SkinportError): SkinportException = when (error.id) {
            "authentication_error" -> AuthenticationError(statusCode, error)
            "validation_error" -> ValidationError(statusCode, error)
            "not_found" -> NotFound(statusCode, error)
            "rate_limit_exceeded" -> RateLimitExceeded(statusCode, error)
            else -> GenericError(statusCode, error)
        }
    }
}

private val json = Json { ignoreUnknownKeys = true }

internal fun createErrorPlugin() = createClientPlugin("SkinportErrorHandling") {
    onResponse { response ->
        if (!response.status.isSuccess()) {
            val rawBody = response.bodyAsText()
            val errorResponse = runCatching {
                json.decodeFromString<SkinportErrorResponse>(rawBody)
            }.getOrElse {
                throw SkinportException.GenericError(response.status.value, SkinportError("unknown", rawBody))
            }

            val error = errorResponse.errors.firstOrNull()
                ?: SkinportError("unknown", "Unknown error")

            throw SkinportException.from(response.status.value, error)
        }
    }
}
