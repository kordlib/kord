package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.rest.json.JsonErrorCode
import com.gitlab.kordlib.rest.json.response.DiscordErrorResponse
import com.gitlab.kordlib.rest.service.RestService
import kotlinx.serialization.Serializable
import io.ktor.client.statement.HttpResponse as KtorResponse

/**
 * Signals that an error related to interactions with the REST API occurred.
 *
 * * [status] &mdash; The HTTP Status code of the failed request.
 * * [error] &mdash; The JSON error body of the failed request, this is optionally present.
 */
abstract class RestRequestException(
        val status: HttpStatus,
        val error: DiscordErrorResponse? = null,
) : RequestException("REST request returned with HTTP ${status.code} ${status.message}.${
    error?.let { " ${error.code}: ${error.message}" } ?: ""
}") {

    @DeprecatedSinceKord("0.7.0")
    @Deprecated(level = DeprecationLevel.WARNING, message = "Use status.code instead", replaceWith = ReplaceWith("status.code"))
    val code: Int by this.status::code

}

/**
 * Represents an HTTP status code and description.
 */
data class HttpStatus(val code: Int, val message: String)

/**
 * Implementation of the [RestRequestException] for [RestServices][RestService] using Ktor.
 */
class KtorRequestException(
        val httpResponse: KtorResponse,
        discordError: DiscordErrorResponse?,
) : RestRequestException(HttpStatus(httpResponse.status.value, httpResponse.status.description), discordError)