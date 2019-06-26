package com.gitlab.hopebaron.rest.request

import com.gitlab.hopebaron.rest.route.Route
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

sealed class Request<T> {
    internal abstract val route: Route<T>
    internal abstract val routeParams: Map<String, Any>
    val identifier: RequestIdentifier by lazy(LazyThreadSafetyMode.NONE) {
        when (route) {
            //https://discordapp.com/developers/docs/topics/rate-limits#rate-limits
            Route.MessageDelete -> MessageDeleteIdentifier(route.path, routeParams[Route.MessageId.identifier].toString())

            else -> MajorIdentifier(route, routeParams)
        }
    }

    abstract fun HttpRequestBuilder.apply()

    open suspend fun parse(response: HttpResponse): T {
        val json = response.readText()
        println(json)
        return Json.nonstrict.parse(route.strategy, json)
    }

    internal tailrec fun generatePath(builder: StringBuilder = StringBuilder(), start: Int = 0): String {
        val indexOfNextParam = route.path.indexOf('{', start)

        return when {
            indexOfNextParam > start -> {
                builder.append(route.path.subSequence(start, indexOfNextParam))
                val indexOfNextParamEnd = route.path.indexOf('}', indexOfNextParam)
                val param = route.path.subSequence(indexOfNextParam, indexOfNextParamEnd + 1)
                builder.append(routeParams[param])
                val nextStart = (indexOfNextParamEnd + 1).coerceAtMost(route.path.length)
                generatePath(builder, nextStart)
            }
            start == 0 -> route.path
            else -> {
                builder.append(route.path.substring(start))
                builder.toString()
            }
        }
    }
}

interface RequestIdentifier

internal class MessageDeleteIdentifier(val path: String, val messageId: String) : RequestIdentifier

internal data class MajorIdentifier(val path: String, val param: String? = null) : RequestIdentifier {

    companion object {
        operator fun invoke(route: Route<*>, routeParams: Map<String, Any>): RequestIdentifier {
            val indexOfNextParam = route.path.indexOf('{')
            if (indexOfNextParam < 0) return MajorIdentifier(route.path)

            val indexOfNextParamEnd = route.path.indexOf('}')
            if (indexOfNextParamEnd < 0) return MajorIdentifier(route.path)

            val param = route.path.subSequence(indexOfNextParam, indexOfNextParamEnd + 1)
            val paramValue = routeParams[param].toString()

            return MajorIdentifier(route.path, paramValue).also {
                println(it)
            }
        }
    }

}

data class RequestBody<T>(val strategy: SerializationStrategy<T>, val body: T) where T : Any

internal class JsonRequest<T>(
        override val route: Route<T>,
        override val routeParams: Map<String, Any>,
        private val parameters: StringValues,
        private val body: RequestBody<*>?
) : Request<T>() {

    override fun HttpRequestBuilder.apply() {
        method = route.method

        url {
            encodedPath += generatePath()
            parameters.appendAll(this@JsonRequest.parameters)
        }

        this@JsonRequest.body?.let {
            val json = Json.nonstrict.stringify(it.strategy as SerializationStrategy<Any>, it.body)
            body = TextContent(json, ContentType.Application.Json)
        }
    }
}

internal class MutlipartRequest<T>(
        override val route: Route<T>,
        override val routeParams: Map<String, Any>,
        private val parameters: StringValues,
        private val body: RequestBody<*>?,
        private val files: List<Pair<String, InputStream>> = emptyList()
) : Request<T>() {
    override fun HttpRequestBuilder.apply() {
        method = route.method

        url {
            encodedPath += generatePath()
            parameters.appendAll(this@MutlipartRequest.parameters)
        }

        val data = formData {

            this@MutlipartRequest.body?.let {
                append("payload_json", Json.nonstrict.stringify(it.strategy as SerializationStrategy<Any>, it.body))
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