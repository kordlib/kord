package com.gitlab.kordlib.rest.ratelimit

import com.gitlab.kordlib.rest.request.Request
import io.ktor.client.statement.HttpResponse

interface RequestHandler {

    suspend fun <B : Any,R>  handle(request: Request<B,R>): R

}