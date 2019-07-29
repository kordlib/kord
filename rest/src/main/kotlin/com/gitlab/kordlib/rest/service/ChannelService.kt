package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.route.Route
import io.ktor.http.Parameters


class ChannelService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun createMessage(channelId: String, message: com.gitlab.kordlib.rest.json.request.MessageCreateRequest) = call(Route.MessageCreate) {
        keys[Route.ChannelId] = channelId
        body(com.gitlab.kordlib.rest.json.request.MessageCreateRequest.serializer(), message)
    }

    suspend fun createMessage(channelId: String, message: com.gitlab.kordlib.rest.json.request.MultipartMessageCreateRequest) = call(Route.MessageCreate) {
        keys[Route.ChannelId] = channelId
        body(com.gitlab.kordlib.rest.json.request.MessageCreateRequest.serializer(), message.request)
        message.files.forEach { file(it) }
    }

    suspend fun getMessages(channelId: String, position: Position? = null, limit: Int = 50) = call(Route.MessagesGet) {
        keys[Route.ChannelId] = channelId
        parameters = Parameters.build {
            if (position != null) {
                append(position.key, position.value)
            }
            append("limit", "$limit")
        }
    }

    suspend fun getMessage(channelId: String, messageId: String) = call(Route.MessageGet) {
        keys[Route.MessageId] = messageId
        keys[Route.ChannelId] = channelId
    }

    suspend fun getChannelInvites(channelId: String) = call(Route.InvitesGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun getChannel(channelId: String) = call(Route.ChannelGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun addPinnedMessage(channelId: String, messageId: String) = call(Route.PinPut) {
        keys[Route.MessageId] = messageId
        keys[Route.ChannelId] = channelId
    }

    suspend fun getChannelPins(channelId: String) = call(Route.PinsGet) {
        keys[Route.ChannelId] = channelId
    }

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

    suspend fun deleteOwnReaction(channelId: String, messageId: String, emoji: String) = call(Route.OwnReactionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji
    }

    suspend fun deletePinnedMessage(channelId: String, messageId: String) = call(Route.PinDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
    }

    suspend fun deleteAllReactions(channelId: String, messageId: String) = call(Route.AllReactionsDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
    }

    suspend fun deleteMessage(channelId: String, messageId: String) = call(Route.MessageDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
    }

    suspend fun bulkDelete(channelId: String, messages: com.gitlab.kordlib.rest.json.request.BulkDeleteRequest) = call(Route.BulkMessageDeletePost) {
        keys[Route.ChannelId] = channelId
        body(com.gitlab.kordlib.rest.json.request.BulkDeleteRequest.serializer(), messages)
    }

    suspend fun deleteChannel(channelId: String) = call(Route.ChannelDelete) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun deleteChannelPermission(channelId: String, overwriteId: String) = call(Route.ChannelPermissionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
    }

    suspend fun editChannelPermissions(channelId: String, overwriteId: String, permissions: com.gitlab.kordlib.rest.json.request.EditChannelPermissionRequest) = call(Route.ChannelPermissionPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        body(com.gitlab.kordlib.rest.json.request.EditChannelPermissionRequest.serializer(), permissions)
    }


    suspend fun getReactions(channelId: String, messageId: String, emoji: String, position: Position? = null, limit: Int = 25) = call(Route.ReactionsGet) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji

        parameters = Parameters.build {
            if (position != null) {
                append(position.key, position.value)
            }
            append("limit", "$limit")

        }
    }

    suspend fun triggerTypingIndicator(channelId: String) = call(Route.TypingIndicatorPost) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun removeFromGroup(channelId: String, userId: String) = call(Route.GroupDMUserDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
    }


    suspend fun addToGroup(channelId: String, userId: String, addUser: com.gitlab.kordlib.rest.json.request.AddDMUserRequest) = call(Route.GroupDMUserPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
        body(com.gitlab.kordlib.rest.json.request.AddDMUserRequest.serializer(), addUser)
    }

    suspend fun createInvite(channelId: String, invite: com.gitlab.kordlib.rest.json.request.InviteCreateRequest) = call(Route.InvitePost) {
        keys[Route.ChannelId] = channelId
        body(com.gitlab.kordlib.rest.json.request.InviteCreateRequest.serializer(), invite)
    }

    suspend fun editMessage(channelId: String, messageId: String, message: com.gitlab.kordlib.rest.json.request.MessageEditRequest) = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(com.gitlab.kordlib.rest.json.request.MessageEditRequest.serializer(), message)
    }

    suspend fun putChannel(channelId: String, channel: com.gitlab.kordlib.rest.json.request.PutModifyChannelRequest) = call(Route.ChannelPut) {
        keys[Route.ChannelId] = channelId
        body(com.gitlab.kordlib.rest.json.request.PutModifyChannelRequest.serializer(), channel)
    }


    suspend fun patchChannel(channelId: String, channel: com.gitlab.kordlib.rest.json.request.PatchModifyChannelRequest) = call(Route.ChannelPatch) {
        keys[Route.ChannelId] = channelId
        body(com.gitlab.kordlib.rest.json.request.PatchModifyChannelRequest.serializer(), channel)
    }

}

