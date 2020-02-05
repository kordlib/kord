package com.gitlab.kordlib.rest

import com.gitlab.kordlib.rest.request.Request
import com.gitlab.kordlib.rest.request.RequestBuilder
import com.gitlab.kordlib.rest.route.Route


internal fun <T> RequestBuilder(route: Route<T>, keySize: Int = 2, request: RequestBuilder<T>.() -> Unit): Request<*,T> {
    val builder = RequestBuilder(route, keySize)
    builder.request()
    return builder.build()
}