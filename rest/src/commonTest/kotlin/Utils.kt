package dev.kord.rest

import dev.kord.rest.request.Request
import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.route.Route


internal fun <T> RequestBuilder(route: Route<T>, keySize: Int = 2, request: RequestBuilder<T>.() -> Unit): Request<*,T> {
    val builder = RequestBuilder(route, keySize)
    builder.request()
    return builder.build()
}