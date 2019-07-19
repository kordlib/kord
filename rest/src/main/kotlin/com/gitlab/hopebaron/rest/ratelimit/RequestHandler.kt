package com.gitlab.hopebaron.rest.ratelimit

import com.gitlab.hopebaron.rest.request.Request
import io.ktor.client.response.HttpResponse

interface RequestHandler {

    suspend fun <T> handle(request: Request<T>): HttpResponse

}