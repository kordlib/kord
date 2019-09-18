package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.request.RequestBuilder
import com.gitlab.kordlib.rest.route.Route

abstract class RestService(protected val requestHandler: RequestHandler) {

    @PublishedApi
    internal suspend inline fun <T> call(route: Route<T>, builder: RequestBuilder<T>.() -> Unit = {}): T {
        val request = RequestBuilder(route).apply(builder).build()

        val response = requestHandler.handle(request)

        return request.parse(response)
    }
}