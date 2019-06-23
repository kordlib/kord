package com.gitlab.hopebaron.rest.request

import com.gitlab.hopebaron.rest.route.Route
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.util.StringValues
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

internal class JsonRequest<T>(
        private val route: Route<T>,
        private val routeParams: Map<String, Any>,
        private val parameters: StringValues,
        private val body: String? = null
) : Request<T> {

    override fun HttpRequestBuilder.apply() {
        method = route.method

        url {
            encodedPath += generatePath()
            parameters.appendAll(this@JsonRequest.parameters)
        }

        this@JsonRequest.body?.let { body = TextContent(it, ContentType.Application.Json) }
    }

    @UnstableDefault
    override suspend fun parse(response: HttpResponse): T {
        val json = response.readText()
        println(json)
        return Json.nonstrict.parse(route.strategy, json)
    }

    private tailrec fun generatePath(builder: StringBuilder = StringBuilder(), start: Int = 0): String {
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

    override val identifier: RequestIdentifier
        get() = TODO()
}
