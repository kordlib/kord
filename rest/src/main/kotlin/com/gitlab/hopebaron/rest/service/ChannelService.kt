package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.common.entity.Message
import com.gitlab.hopebaron.rest.json.request.MessageCreateRequest
import com.gitlab.hopebaron.rest.json.request.MultipartMessageCreateRequest
import com.gitlab.hopebaron.rest.ratelimit.ExclusionRequestHandler
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.request.JsonRequest
import com.gitlab.hopebaron.rest.request.MutlipartRequest
import com.gitlab.hopebaron.rest.request.Request
import com.gitlab.hopebaron.rest.request.RequestBody
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.header
import io.ktor.util.StringValues
import kotlinx.io.InputStream
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.UnstableDefault

class ChannelService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getMessage(channelId: String, messageId: String): Message = call(Route.MessageGet) {
        keys[Route.MessageId] = messageId
        keys[Route.ChannelId] = channelId
    }

    suspend fun createMessage(channelId: String, message: MessageCreateRequest): Message = call(Route.MessageCreate) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), message)
    }

    suspend fun createMessage(channelId: String, message: MultipartMessageCreateRequest): Message = call(Route.MessageCreate) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), message.request)

        message.files.forEach { file(it) }
    }

}

