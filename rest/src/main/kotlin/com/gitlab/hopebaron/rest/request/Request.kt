package com.gitlab.hopebaron.rest.request

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.response.HttpResponse

interface Request<T> {
    fun HttpRequestBuilder.apply()

    val identifier: RequestIdentifier

    suspend fun parse(response: HttpResponse): T
}

interface RequestIdentifier
