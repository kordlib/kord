package com.gitlab.kordlib.rest.request

import io.ktor.client.statement.HttpResponse

abstract class RequestException(val code: Int, message: String) : Exception(message)

class KtorRequestException(val response: HttpResponse, message: String) : RequestException(response.status.value, message)