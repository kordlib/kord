package dev.kord.rest.request

import dev.kord.rest.NamedFile
import dev.kord.rest.route.Route
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

public sealed class Request<B : Any, R> {
    public abstract val route: Route<R>
    public abstract val routeParams: Map<Route.Key, String>
    public abstract val headers: StringValues
    public abstract val parameters: StringValues
    public abstract val body: RequestBody<B>?
    public abstract val files: List<NamedFile>?

    public val path: String
        get() {
            var path = route.path
            routeParams.forEach { (k, v) -> path = path.replaceFirst(k.identifier, v.encodeURLQueryComponent()) }
            return path
        }
}

public val Request<*, *>.identifier: RequestIdentifier
    get() = when { //The major identifier is always the 'biggest' entity.
        Route.GuildId in routeParams -> RequestIdentifier.MajorParamIdentifier(
            route,
            routeParams.getValue(Route.GuildId)
        )
        Route.ChannelId in routeParams -> RequestIdentifier.MajorParamIdentifier(
            route,
            routeParams.getValue(Route.ChannelId)
        )
        Route.WebhookId in routeParams -> RequestIdentifier.MajorParamIdentifier(
            route,
            routeParams.getValue(Route.WebhookId)
        )
        else -> RequestIdentifier.RouteIdentifier(route)
    }

/**
 * A ['per-route'](https://discord.com/developers/docs/topics/rate-limits) identifier for rate limiting purposes.
 */
public sealed class RequestIdentifier {
    /**
     * An identifier that does not contain any major parameters.
     */
    public data class RouteIdentifier(val route: Route<*>) : RequestIdentifier()

    /**
     * An identifier with a major parameter.
     */
    public data class MajorParamIdentifier(val route: Route<*>, val param: String) : RequestIdentifier()
}

public data class RequestBody<T>(val strategy: SerializationStrategy<T>, val body: T) where T : Any

public class JsonRequest<B : Any, R>(
    override val route: Route<R>,
    override val routeParams: Map<Route.Key, String>,
    override val parameters: StringValues,
    override val headers: StringValues,
    override val body: RequestBody<B>?
) : Request<B, R>() {
    override val files: List<NamedFile>? = null
}

public class MultipartRequest<B : Any, R>(
    override val route: Route<R>,
    override val routeParams: Map<Route.Key, String>,
    override val parameters: StringValues,
    override val headers: StringValues,
    override val body: RequestBody<B>?,
    override val files: List<NamedFile> = emptyList()
) : Request<B, R>() {

    public val data: List<PartData> = formData {
        body?.let {
            append("payload_json", Json.encodeToString(it.strategy, it.body))
        }
        try {

            files.forEachIndexed { index, pair ->

                val (name, inputStream) = pair
                append(
                    "file$index",
                    inputStream.readBytes(),
                    Headers.build { append(HttpHeaders.ContentDisposition, "filename=$name") }
                )
            }
        } finally {
            files.forEach { it.inputStream.close() }
        }
    }
}
