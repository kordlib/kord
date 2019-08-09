package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.rest.route.Route
import io.ktor.http.HeadersBuilder
import io.ktor.http.ParametersBuilder
import kotlinx.io.InputStream
import kotlinx.serialization.SerializationStrategy

class RequestBuilder<T>(private val route: Route<T>, keySize: Int = 2) {

    var keys: MutableMap<String, String> = HashMap(keySize, 1f)

    private val headers = HeadersBuilder()
    private val parameters = ParametersBuilder()

    private var body: RequestBody<*>? = null
    private var files: MutableList<Pair<String, InputStream>>? = null

    private fun initFiles() {
        if (files == null) files = mutableListOf()
    }

    operator fun MutableMap<String, String>.set(key: Route.Key, value: String) = set(key.identifier, value)

    fun <E : Any> body(strategy: SerializationStrategy<E>, body: E) {
        this.body = RequestBody(strategy, body)
    }

    fun parameter(key: String, value: String) = parameters.append(key, value)

    fun header(key: String, value: String) = headers.append(key, value)

    fun file(name: String, input: InputStream) {
        initFiles()
        files!!.add(name to input)
    }

    fun file(pair: Pair<String, InputStream>) {
        initFiles()
        files!!.add(pair)
    }

    fun build(): Request<T> = if (files == null) {
        JsonRequest(route, keys, parameters.build(), headers.build(), body)
    } else {
        MultipartRequest(route, keys, parameters.build(), headers.build(), body, files.orEmpty())
    }

}
