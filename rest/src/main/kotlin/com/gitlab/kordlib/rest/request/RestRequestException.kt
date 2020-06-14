package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.common.exception.RequestException
import io.ktor.client.statement.HttpResponse

/**
 * Signals that an error related to interactions with the REST API occurred.
 */
abstract class RestRequestException(val code: Int, message: String) : RequestException(message)

class KtorRequestException(val response: HttpResponse, message: String) : RestRequestException(response.status.value, message)