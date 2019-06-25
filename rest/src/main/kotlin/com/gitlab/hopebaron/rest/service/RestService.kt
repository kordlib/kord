package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route

abstract class RestService(protected val requestHandler: RequestHandler) {
    protected suspend fun <T> call(route: Route<T>, builder: RequestBuilder<T>.() -> Unit): T {
        val request = RequestBuilder(route).apply(builder).build()

        val response = requestHandler.handle(request)

        return request.parse(response)
    }
}