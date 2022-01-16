package dev.kord.rest.request

import dev.kord.common.entity.Snowflake
import dev.kord.rest.NamedFile
import dev.kord.rest.route.Route
import io.ktor.http.*
import kotlinx.serialization.SerializationStrategy

class RequestBuilder<T>(private val route: Route<T>, keySize: Int = 2) {

    val keys: MutableMap<Route.Key, String> = HashMap(keySize, 1f)

    operator fun MutableMap<Route.Key, String>.set(key: Route.Key, value: Snowflake) = set(key, value.toString())

    private val headers = HeadersBuilder()
    private val parameters = ParametersBuilder()

    private var body: RequestBody<*>? = null
    private val files: MutableList<NamedFile> = mutableListOf()

    operator fun MutableMap<String, String>.set(key: Route.Key, value: String) = set(key.identifier, value)

    fun <E : Any> body(strategy: SerializationStrategy<E>, body: E) {
        this.body = RequestBody(strategy, body)
    }

    fun parameter(key: String, value: Snowflake) = parameters.append(key, value.value.toString())

    fun parameter(key: String, value: Any) = parameters.append(key, value.toString())

    @Deprecated(
        "'header' was renamed to 'urlEncodedHeader'",
        ReplaceWith("urlEncodedHeader(key, value)"),
        DeprecationLevel.ERROR,
    )
    fun header(key: String, value: String) = urlEncodedHeader(key, value)

    /** Adds a header and encodes its [value] as an [URL query component][encodeURLQueryComponent]. */
    fun urlEncodedHeader(key: String, value: String) {
        headers.append(key, value.encodeURLQueryComponent())
    }

    /** Adds a header without encoding its [value]. */
    fun unencodedHeader(key: String, value: String) {
        headers.append(key, value)
    }

    fun file(name: String, input: java.io.InputStream) {
        files.add(NamedFile(name, input))
    }

    fun file(file: NamedFile) {
        files.add(file)
    }

    fun build(): Request<*, T> = when {
        files.isEmpty() -> JsonRequest(route, keys, parameters.build(), headers.build(), body)
        else -> MultipartRequest(route, keys, parameters.build(), headers.build(), body, files.orEmpty())
    }

}
