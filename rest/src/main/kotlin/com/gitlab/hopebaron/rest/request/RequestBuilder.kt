package com.gitlab.hopebaron.rest.request

import com.gitlab.hopebaron.rest.route.Route
import io.ktor.util.StringValues
import kotlinx.io.InputStream
import kotlinx.serialization.SerializationStrategy

class RequestBuilder<T>(private val route: Route<T>) {

    val keys: MutableMap<Route.Key, String> by lazy { mutableMapOf<Route.Key, String>() }

    var parameters = StringValues.Empty
    private var body: RequestBody<*>? = null
    private val files: MutableList<Pair<String, InputStream>> by lazy { mutableListOf<Pair<String, InputStream>>() }

    fun <E : Any> body(strategy: SerializationStrategy<E>, body: E) {
        this.body = RequestBody(strategy, body)
    }

    fun file(name: String, input: InputStream) {
        files.add(name to input)
    }

    fun file(pair: Pair<String, InputStream>) {
        files.add(pair)
    }

    fun build(): Request<T> = if (files.isEmpty()) {
        JsonRequest(route, keys.mapKeys { it.key.identifier }, parameters, body)
    } else {
        MutlipartRequest(route, keys.mapKeys { it.key.identifier }, parameters, body, files)

    }
}