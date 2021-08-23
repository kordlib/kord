package dev.kord.rest.request

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.exception.RequestException
import dev.kord.rest.json.response.DiscordErrorResponse
import dev.kord.rest.service.RestService
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import io.ktor.client.statement.HttpResponse as KtorResponse

private fun formatRestRequestExceptionMessage(
    status: HttpStatus,
    error: DiscordErrorResponse?
): String {
    val statusCode = status.code
    val statusMessage = status.message
    val errorMessage = error?.let { " ${error.message} ${error.errors}" } ?: ""

    return "REST request returned an error: $statusCode $statusMessage $errorMessage"
}

/**
 * Signals that an error related to interactions with the REST API occurred.
 *
 * * [status] &mdash; The HTTP Status code of the failed request.
 * * [error] &mdash; The JSON error body of the failed request, this is optionally present.
 */
abstract class RestRequestException(
    val request: Request<*, *>,
    val status: HttpStatus,
    val error: DiscordErrorResponse? = null,
) : RequestException(formatRestRequestExceptionMessage(status, error)) {

    @DeprecatedSinceKord("0.7.0")
    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "Use status.code instead",
        replaceWith = ReplaceWith("status.code")
    )
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
    request: Request<*, *>,
    discordError: DiscordErrorResponse?,
) : RestRequestException(request, HttpStatus(httpResponse.status.value, httpResponse.status.description), discordError)
