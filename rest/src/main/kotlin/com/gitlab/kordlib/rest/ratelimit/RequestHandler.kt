package com.gitlab.kordlib.rest.ratelimit

import com.gitlab.kordlib.rest.request.Request
import io.ktor.client.statement.HttpResponse

interface RequestHandler {

    suspend fun <T> handle(request: Request<T>): HttpResponse

}