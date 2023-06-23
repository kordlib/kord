package dev.kord.rest.request

import dev.kord.common.exception.RequestException
import dev.kord.rest.json.response.DiscordErrorResponse
import dev.kord.rest.service.RestService
import io.ktor.client.statement.HttpResponse

private fun formatRestRequestExceptionMessage(status: HttpStatus, error: DiscordErrorResponse?): String {
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
public abstract class RestRequestException(
    public val request: Request<*, *>,
    public val status: HttpStatus,
    public val error: DiscordErrorResponse? = null,
) : RequestException(formatRestRequestExceptionMessage(status, error))

/**
 * Represents an HTTP status code and description.
 */
public data class HttpStatus(val code: Int, val message: String)

/**
 * Implementation of the [RestRequestException] for [RestServices][RestService] using Ktor.
 */
public class KtorRequestException(
    @Suppress("MemberVisibilityCanBePrivate") public val httpResponse: HttpResponse,
    request: Request<*, *>,
    discordError: DiscordErrorResponse?,
) : RestRequestException(request, HttpStatus(httpResponse.status.value, httpResponse.status.description), discordError)
