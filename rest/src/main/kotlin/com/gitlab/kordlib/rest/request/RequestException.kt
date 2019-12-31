package com.gitlab.kordlib.rest.request

import io.ktor.client.statement.HttpResponse

class RequestException internal constructor(val response: HttpResponse, message: String) : Exception(message)