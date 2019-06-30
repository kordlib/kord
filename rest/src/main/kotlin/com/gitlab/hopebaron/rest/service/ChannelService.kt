package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.json.request.*
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Position
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.http.Parameters


class ChannelService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun createMessage(channelId: String, message: MessageCreateRequest) = call(Route.MessageCreate) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), message)
    }

    suspend fun createMessage(channelId: String, message: MultipartMessageCreateRequest) = call(Route.MessageCreate) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), message.request)
        message.files.forEach { file(it) }
    }

    suspend fun getMessages(channelId: String, position: Position? = null, limit: Int = 50) = call(Route.MessagesGet) {
        keys[Route.ChannelId] = channelId
        if (position != null) {
            parameters = Parameters.build {
                append(position.key, position.value)
                append("limit", "$limit")
            }
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

    suspend fun bulkDelete(channelId: String, messages: BulkDeleteRequest) = call(Route.BulkMessageDeletePost) {
        keys[Route.ChannelId] = channelId
        body(BulkDeleteRequest.serializer(), messages)
    }

    suspend fun deleteChannel(channelId: String) = call(Route.ChannelDelete) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun deleteChannelPermission(channelId: String, overwriteId: String) = call(Route.ChannelPermissionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
    }

    suspend fun editChannelPermissions(channelId: String, overwriteId: String, permissions: EditChannelPermissionRequest) = call(Route.ChannelPermissionPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        body(EditChannelPermissionRequest.serializer(), permissions)
    }


    suspend fun getReactions(channelId: String, messageId: String, emoji: String, position: Position? = null, limit: Int = 25) = call(Route.ReactionsGet) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji

        if (position != null) {
            parameters = Parameters.build {
                append(position.key, position.value)
                append("limit", "$limit")
            }
        }
    }

    suspend fun triggerTypingIndicator(channelId: String) = call(Route.TypingIndicatorPost) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun removeFromGroup(channelId: String, userId: String) = call(Route.GroupDMUserDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
    }


    suspend fun addToGroup(channelId: String, userId: String, addUser: AddDMUserRequest) = call(Route.GroupDMUserPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
        body(AddDMUserRequest.serializer(), addUser)
    }

    suspend fun createInvite(channelId: String, invite: InviteCreateRequest) = call(Route.InvitePost) {
        keys[Route.ChannelId] = channelId
        body(InviteCreateRequest.serializer(), invite)
    }

    suspend fun editMessage(channelId: String, messageId: String, message: MessageEditRequest) = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(MessageEditRequest.serializer(), message)
    }

    suspend fun putChannel(channelId: String, channel: PutModifyChannelRequest) = call(Route.ChannelPut) {
        keys[Route.ChannelId] = channelId
        body(PutModifyChannelRequest.serializer(), channel)
    }


    suspend fun patchChannel(channelId: String, channel: PatchModifyChannelRequest) = call(Route.ChannelPatch) {
        keys[Route.ChannelId] = channelId
        body(PatchModifyChannelRequest.serializer(), channel)
    }

}

