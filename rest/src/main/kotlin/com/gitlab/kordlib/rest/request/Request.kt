package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.util.StringValues
import kotlinx.io.InputStream
import kotlinx.io.streams.outputStream
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

@Suppress("EXPERIMENTAL_API_USAGE")
private val parser = Json(JsonConfiguration(encodeDefaults = false, strictMode = false))

sealed class Request<T> {
    abstract val route: Route<T>
    internal abstract val routeParams: Map<Route.Key, String>
    val identifier: RequestIdentifier by lazy(LazyThreadSafetyMode.NONE) {
        when (route) {
            //https://discordapp.com/developers/docs/topics/rate-limits#rate-limits
            Route.MessageDelete -> MessageDeleteIdentifier(route.path, routeParams[Route.MessageId]!!)
            else -> MajorIdentifier(route, routeParams)
        }
    }

    abstract fun HttpRequestBuilder.apply()

    open suspend fun parse(response: HttpResponse): T {
        val json = response.readText()
        logger.trace { "${response.call.request.method.value} ${response.call.request.url} body: $json" }
        return parser.parse(route.strategy, json)
    }

    internal fun generatePath(): String {
        var path = route.path
        routeParams.forEach { (k, v) -> path = path.replaceFirst(k.identifier, v) }
        return path
    }
}

interface RequestIdentifier

internal class MessageDeleteIdentifier(val path: String, val messageId: String) : RequestIdentifier
internal data class MajorIdentifier(val path: String, val param: String? = null) : RequestIdentifier {

    companion object {
        operator fun invoke(route: Route<*>, routeParams: Map<Route.Key, String>): RequestIdentifier {
            with(route.path) {
                val start = indexOf('{')
                val end = indexOf('}')

                if (start < 0 || end < 0) return MajorIdentifier(this)

                val param = subSequence(start..end)
                val entry = routeParams.entries.firstOrNull { (k) -> param == k.identifier && k.isMajor }
                return if (entry != null) MajorIdentifier(this, entry.value)
                else MajorIdentifier(this)
            }
        }
    }

}

data class RequestBody<T>(val strategy: SerializationStrategy<T>, val body: T) where T : Any

internal class JsonRequest<T>(
        override val route: Route<T>,
        override val routeParams: Map<Route.Key, String>,
        private val parameters: StringValues,
        private val headers: StringValues,
        private val body: RequestBody<*>?
) : Request<T>() {

    override fun HttpRequestBuilder.apply() {
        method = route.method

        url {
            encodedPath += generatePath()
            parameters.appendAll(this@JsonRequest.parameters)
            headers.appendAll(this@JsonRequest.headers)
        }

        this@JsonRequest.body?.let {
            val json = parser.stringify(it.strategy as SerializationStrategy<Any>, it.body)
            body = TextContent(json, ContentType.Application.Json)
        }
    }
}

internal class MultipartRequest<T>(
        override val route: Route<T>,
        override val routeParams: Map<Route.Key, String>,
        private val parameters: StringValues,
        private val headers: StringValues,
        private val body: RequestBody<*>?,
        private val files: List<Pair<String, InputStream>> = emptyList()
) : Request<T>() {
    override fun HttpRequestBuilder.apply() {
        method = route.method

        url {
            encodedPath += generatePath()
            parameters.appendAll(this@MultipartRequest.parameters)
            headers.appendAll(this@MultipartRequest.headers)
        }

        val data = formData {

            this@MultipartRequest.body?.let {
                append("payload_json", parser.stringify(it.strategy as SerializationStrategy<Any>, it.body))
            }

            if (files.size == 1) append("file", filename = files[0].first) {
                files[0].second.copyTo(outputStream())
            }
            else files.forEachIndexed { index, pair ->
                append("file$index", pair.first) { pair.second.copyTo(outputStream()) }
            }
        }

        body = MultiPartFormDataContent(data)
    }
}