package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.request.RequestBuilder
import com.gitlab.kordlib.rest.route.Route

abstract class RestService(@PublishedApi internal val requestHandler: RequestHandler) {

    @PublishedApi
    internal suspend inline fun <T> call(route: Route<T>, builder: RequestBuilder<T>.() -> Unit = {}): T {
        val request = RequestBuilder(route).apply(builder).build()
        return requestHandler.handle(request)
    }

}

