package dev.kord.rest.request

import dev.kord.common.entity.Snowflake
import dev.kord.rest.NamedFile
import dev.kord.rest.route.Route
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.cio.*
import kotlinx.serialization.SerializationStrategy
import java.io.InputStream
import java.nio.file.Path
import kotlin.DeprecationLevel.HIDDEN

public class RequestBuilder<T>(public val route: Route<T>, keySize: Int = 2) {

    public var baseUrl: String = Route.baseUrl
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

    /** Adds a header and encodes its [value] as an [URL query component][encodeURLQueryComponent]. */
    public fun urlEncodedHeader(key: String, value: String) {
        headers.append(key, value.encodeURLQueryComponent())
    }

    /** Adds a header without encoding its [value]. */
    public fun unencodedHeader(key: String, value: String) {
        headers.append(key, value)
    }

    /** @suppress */
    @Deprecated(
        "Use lazy ChannelProvider instead of InputStream. You should also make sure that the stream/channel is only " +
                "opened inside the block of the ChannelProvider because it could otherwise be read multiple times " +
                "(which isn't allowed).",
        ReplaceWith(
            "file(name, ChannelProvider { content.toByteReadChannel() })",
            "io.ktor.client.request.forms.ChannelProvider",
            "io.ktor.utils.io.jvm.javaio.toByteReadChannel",
        ),
        level = HIDDEN,
    )
    @Suppress("DEPRECATION_ERROR")
    public fun file(name: String, input: InputStream) {
        files.add(NamedFile(input, name))
    }

    public fun file(path: Path) {
        file(path.fileName.toString(), ChannelProvider { path.readChannel() })
    }

    public fun file(name: String, contentProvider: ChannelProvider) {
        files.add(NamedFile(name, contentProvider))
    }

    public fun file(file: NamedFile) {
        files.add(file)
    }

    public fun build(): Request<*, T> = when {
        files.isEmpty() -> JsonRequest(route, keys, parameters.build(), headers.build(), body, baseUrl)
        else -> MultipartRequest(route, keys, parameters.build(), headers.build(), body, files, baseUrl)
    }
}
