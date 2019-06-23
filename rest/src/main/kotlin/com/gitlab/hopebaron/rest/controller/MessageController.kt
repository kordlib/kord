package com.gitlab.hopebaron.rest.controller

import com.gitlab.hopebaron.common.entity.Message
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Authorization
import com.gitlab.hopebaron.rest.route.JsonRequest
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.util.StringValues
import kotlinx.serialization.UnstableDefault

@UnstableDefault
class MessageController(private val client: HttpClient, private val authorization: Authorization, private val requestHandler: RequestHandler) {

    suspend fun getMessage(channelId: String, messageId: String): Message {
        val parameters = mapOf(Route.ChannelId.identifier to channelId, Route.MessageId.identifier to messageId)
        val route = Route.MessageGet
        val request = JsonRequest(route, parameters, StringValues.Empty, null)

        val response = client.request<HttpResponse>(Route.baseUrl) {
            with(authorization) { apply() }
            with(request) { apply() }

            println(url.buildString())
        }

        return request.parse(response)
    }

    suspend fun createMessage(channelId: String, message: String): Message {
        val parameters = mapOf(Route.ChannelId.identifier to channelId)
        val route = Route.MessageCreate
        val request = JsonRequest(route, parameters, StringValues.Empty, """
{
   "content":"$message"
}
""".trimIndent())

        val response = client.request<HttpResponse>(Route.baseUrl) {
            with(authorization) { apply() }
            with(request) { apply() }
        }

        return request.parse(response)
    }
}
