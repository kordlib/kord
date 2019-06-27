package com.gitlab.hopebaron.rest.request

import com.gitlab.hopebaron.rest.route.Route
import io.ktor.util.StringValues
import kotlinx.io.InputStream
import kotlinx.serialization.SerializationStrategy

class RequestBuilder<T>(private val route: Route<T>, keySize: Int = 2) {

    var keys: MutableMap<String, String> = HashMap(keySize, 1f)

    var parameters = StringValues.Empty
    private var body: RequestBody<*>? = null
    private var files: MutableList<Pair<String, InputStream>>? = null

    private fun initFiles() {
        if (files == null) files = mutableListOf()
    }

    operator fun MutableMap<String, String>.set(key: Route.Key, value: String) = set(key.identifier, value)

    fun <E : Any> body(strategy: SerializationStrategy<E>, body: E) {
        this.body = RequestBody(strategy, body)
    }

    fun file(name: String, input: InputStream) {
        initFiles()
        files!!.add(name to input)
    }

    fun file(pair: Pair<String, InputStream>) {
        initFiles()
        files!!.add(pair)
    }

    fun build(): Request<T> = if (files == null) {
        JsonRequest(route, keys, parameters, body)
    } else {
        MutlipartRequest(route, keys, parameters, body, files.orEmpty())
    }
}
