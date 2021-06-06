package dev.kord.rest.request

import dev.kord.rest.route.Route
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.http.encodeURLQueryComponent
import io.ktor.util.StringValues
import io.ktor.utils.io.streams.outputStream
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

sealed class Request<B : Any, R> {
    abstract val route: Route<R>
    abstract val routeParams: Map<Route.Key, String>
    abstract val headers: StringValues
    abstract val parameters: StringValues
    abstract val body: RequestBody<B>?
    abstract val files: List<Pair<String, java.io.InputStream>>?

    val path: String
        get() {
            var path = route.path
            routeParams.forEach { (k, v) -> path = path.replaceFirst(k.identifier, v.encodeURLQueryComponent()) }
            return path
        }
}

val Request<*, *>.identifier
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
sealed class RequestIdentifier {
    /**
     * An identifier that does not contain any major parameters.
     */
    data class RouteIdentifier(val route: Route<*>) : RequestIdentifier()

    /**
     * An identifier with a major parameter.
     */
    data class MajorParamIdentifier(val route: Route<*>, val param: String) : RequestIdentifier()
}

data class RequestBody<T>(val strategy: SerializationStrategy<T>, val body: T) where T : Any

class JsonRequest<B : Any, R>(
    override val route: Route<R>,
    override val routeParams: Map<Route.Key, String>,
    override val parameters: StringValues,
    override val headers: StringValues,
    override val body: RequestBody<B>?
) : Request<B, R>() {
    override val files: List<Pair<String, java.io.InputStream>>? = null
}

class MultipartRequest<B : Any, R>(
    override val route: Route<R>,
    override val routeParams: Map<Route.Key, String>,
    override val parameters: StringValues,
    override val headers: StringValues,
    override val body: RequestBody<B>?,
    override val files: List<Pair<String, java.io.InputStream>> = emptyList()
) : Request<B, R>() {

    val data = formData {
        body?.let {
            append("payload_json", Json.encodeToString(it.strategy, it.body))
        }
        try {
            if (files.size == 1) append("file", filename = files[0].first) {
                files[0].second.copyTo(outputStream())
            } else files.forEachIndexed { index, pair ->
                val name = pair.first
                val inputStream = pair.second
                append("file$index", name) { inputStream.copyTo(outputStream()) }
            }
        } finally {
            files.forEach { it.second.close() }
        }
    }

}
