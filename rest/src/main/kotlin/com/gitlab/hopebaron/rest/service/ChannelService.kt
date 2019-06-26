package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.common.entity.Message
import com.gitlab.hopebaron.rest.json.request.MessageCreateRequest
import com.gitlab.hopebaron.rest.json.request.MultipartMessageCreateRequest
import com.gitlab.hopebaron.rest.json.response.InviteResponse
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.util.InternalAPI
import io.ktor.util.StringValues

class ChannelService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun createMessage(channelId: String, message: MessageCreateRequest): Message = call(Route.MessageCreate) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), message)
    }

    suspend fun createMessage(channelId: String, message: MultipartMessageCreateRequest): Message = call(Route.MessageCreate) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), message.request)
        message.files.forEach { file(it) }
    }

    @InternalAPI
    suspend fun getMessages(channelId: String, limit: Int = 50): List<Message> = call(Route.MessagesGet) {
        keys[Route.ChannelId] = channelId
        parameters = StringValues.build {
            append("limit", "$limit")
        }
    }

    @InternalAPI
    suspend fun getMessagesAfter(channelId: String, messageId: String, limit: Int = 50): List<Message> = call(Route.MessagesGet) {
        keys[Route.ChannelId] = channelId
        parameters = StringValues.build {
            append("after", messageId)
            append("limit", "$limit")
        }
    }

    @InternalAPI
    suspend fun getMessagesAround(channelId: String, messageId: String, limit: Int = 50): List<Message> = call(Route.MessagesGet) {
        keys[Route.ChannelId] = channelId
        parameters = StringValues.build {
            append("around", messageId)
            append("limit", "$limit")
        }
    }

    @InternalAPI
    suspend fun getMessagesBefore(channelId: String, messageId: String, limit: Int = 50): List<Message> = call(Route.MessagesGet) {
        keys[Route.ChannelId] = channelId
        parameters = StringValues.build {
            append("before", messageId)
            append("limit", "$limit")
        }
    }

    suspend fun getMessage(channelId: String, messageId: String): Message = call(Route.MessageGet) {
        keys[Route.MessageId] = messageId
        keys[Route.ChannelId] = channelId
    }

    suspend fun getChannelInvites(channelId: String): List<InviteResponse> = call(Route.InvitesGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun getChannelPins(channelId: String): List<Message> = call(Route.PinsGet) {
        keys[Route.ChannelId] = channelId
    }

    //TODO Check how emoji should be handled
    suspend fun createReaction(channelId: String, messageId: String, emoji: String) = call(Route.ReactionPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji
    }

    suspend fun deleteReaction(channelId: String, messageId: String, userId: String, emoji: String) = call(Route.ReactionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji
        keys[Route.UserId] = userId
    }

    suspend fun deleteOwnReaction(channelId: String, messageId: String, emoji: String) = call(Route.ReactionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji
    }

    suspend fun deletePinnedMessage(channelId: String, messageId: String) = call(Route.PinDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
    }

    suspend fun deleteAllMessageReactions(channelId: String, messageId: String) = call(Route.AllReactionsDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
    }

    suspend fun deleteChannelPermission(channelId: String, overwriteId: String) = call(Route.ChannelPermissionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
    }
}

