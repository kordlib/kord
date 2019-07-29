package com.gitlab.kordlib.rest.request

import io.ktor.client.response.HttpResponse

class RequestException internal constructor(val response: HttpResponse, message: String) : Exception(message)