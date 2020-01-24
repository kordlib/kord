package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.encodeURLQueryComponent
import io.ktor.util.StringValues
import io.ktor.utils.io.streams.outputStream
import kotlinx.io.InputStream
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }


sealed class Request<B : Any, R> {
    abstract val route: Route<R>
    abstract val routeParams: Map<Route.Key, String>
    abstract val headers: StringValues
    abstract val parameters: StringValues
    abstract val body: RequestBody<B>?
    abstract val files: List<Pair<String, InputStream>>?
    val identifier: RequestIdentifier by lazy(LazyThreadSafetyMode.NONE) {
        when (route) {
            //https://discordapp.com/developers/docs/topics/rate-limits#rate-limits
            Route.MessageDelete -> MessageDeleteIdentifier(route.path, routeParams[Route.MessageId]!!)
            else -> MajorIdentifier(route, routeParams)
        }
    }

val path: String get() {
        var path = route.path
        routeParams.forEach { (k, v) -> path = path.replaceFirst(k.identifier, v.encodeURLQueryComponent()) }
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

 class JsonRequest<B : Any, R>(
        override val route: Route<R>,
        override val routeParams: Map<Route.Key, String>,
        override val parameters: StringValues,
        override val headers: StringValues,
        override val body: RequestBody<B>?
) : Request<B, R>() {
    override val files: List<Pair<String, InputStream>>? = null
}

 class MultipartRequest<B : Any, R>(
        override val route: Route<R>,
        override val routeParams: Map<Route.Key, String>,
        override val parameters: StringValues,
        override val headers: StringValues,
        override val body: RequestBody<B>?,
        override val files: List<Pair<String, InputStream>> = emptyList()
) : Request<B, R>() {

         val data = formData {


             if (files.size == 1) append("file", filename = files[0].first) {
                 files[0].second.copyTo(outputStream())
             } else files.forEachIndexed { index, pair ->
                 val name = pair.first
                 val inputStream = pair.second
                 append("file$index", name) { inputStream.copyTo(outputStream()) }
             }
         }

     }




