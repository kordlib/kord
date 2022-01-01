package dev.kord.rest.request

import dev.kord.common.entity.Snowflake
import dev.kord.rest.NamedFile
import dev.kord.rest.route.Route
import io.ktor.http.*
import kotlinx.serialization.SerializationStrategy

public class RequestBuilder<T>(private val route: Route<T>, keySize: Int = 2) {

    public val keys: MutableMap<Route.Key, String> = HashMap(keySize, 1f)

    public operator fun MutableMap<Route.Key, String>.set(key: Route.Key, value: Snowflake) {
        set(key, value.toString())
    }

    private val headers = HeadersBuilder()
    private val parameters = ParametersBuilder()

    private var body: RequestBody<*>? = null
    private val files: MutableList<NamedFile> = mutableListOf()

    public operator fun MutableMap<String, String>.set(key: Route.Key, value: String) {
        set(key.identifier, value)
    }

    public fun <E : Any> body(strategy: SerializationStrategy<E>, body: E) {
        this.body = RequestBody(strategy, body)
    }

    public fun parameter(key: String, value: Snowflake) {
        parameters.append(key, value.value.toString())
    }

    public fun parameter(key: String, value: Any) {
        parameters.append(key, value.toString())
    }

    public fun header(key: String, value: String) {
        headers.append(key, value.encodeURLQueryComponent())
    }

    public fun file(name: String, input: java.io.InputStream) {
        files.add(NamedFile(name, input))
    }

    public fun file(file: NamedFile) {
        files.add(file)
    }

    public fun build(): Request<*, T> = when {
        files.isEmpty() -> JsonRequest(route, keys, parameters.build(), headers.build(), body)
        else -> MultipartRequest(route, keys, parameters.build(), headers.build(), body, files)
    }
}
