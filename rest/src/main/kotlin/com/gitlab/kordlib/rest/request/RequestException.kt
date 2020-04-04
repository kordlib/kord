package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.rest.json.response.DiscordErrorResponse
import io.ktor.client.statement.HttpResponse

abstract class RequestException(val code: Int, message: String) : Exception(message)

class KtorRequestException(val response: HttpResponse, message: String, error: DiscordErrorResponse) : RequestException(response.status.value, message)