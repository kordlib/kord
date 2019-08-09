package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.route.Route


class ChannelService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun createMessage(channelId: String, message: MessageCreatePostRequest) = call(Route.MessagePost) {
        keys[Route.ChannelId] = channelId
        body(MessageCreatePostRequest.serializer(), message)
    }

    suspend fun createMessage(channelId: String, message: MultipartMessageCreatePostRequest) = call(Route.MessagePost) {
        keys[Route.ChannelId] = channelId
        body(MessageCreatePostRequest.serializer(), message.request)
        message.files.forEach { file(it) }
    }

    suspend fun getMessages(channelId: String, position: Position? = null, limit: Int = 50) = call(Route.MessagesGet) {
        keys[Route.ChannelId] = channelId
            if (position != null) {
                parameter(position.key, position.value)
            }
        parameter("limit", "$limit")
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

    suspend fun deleteMessage(channelId: String, messageId: String, reason: String? = null) = call(Route.MessageDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun bulkDelete(channelId: String, messages: BulkDeleteRequest) = call(Route.BulkMessageDeletePost) {
        keys[Route.ChannelId] = channelId
        body(BulkDeleteRequest.serializer(), messages)
    }

    suspend fun deleteChannel(channelId: String, reason: String? = null) = call(Route.ChannelDelete) {
        keys[Route.ChannelId] = channelId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteChannelPermission(channelId: String, overwriteId: String, reason: String? = null) = call(Route.ChannelPermissionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun editChannelPermissions(channelId: String, overwriteId: String, permissions: ChannelPermissionEditPutRequest, reason: String? = null) = call(Route.ChannelPermissionPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        body(ChannelPermissionEditPutRequest.serializer(), permissions)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }


    suspend fun getReactions(channelId: String, messageId: String, emoji: String, position: Position? = null, limit: Int = 25) = call(Route.ReactionsGet) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji

        if (position != null) {
            parameter(position.key, position.value)
            }
        parameter("limit", "$limit")


    }

    suspend fun triggerTypingIndicator(channelId: String) = call(Route.TypingIndicatorPost) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun removeFromGroup(channelId: String, userId: String) = call(Route.GroupDMUserDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
    }


    suspend fun addToGroup(channelId: String, userId: String, addUser: UserAddDMPutRequest) = call(Route.GroupDMUserPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
        body(UserAddDMPutRequest.serializer(), addUser)
    }

    suspend fun createInvite(channelId: String, invite: InviteCreatePostRequest, reason: String? = null) = call(Route.InvitePost) {
        keys[Route.ChannelId] = channelId
        body(InviteCreatePostRequest.serializer(), invite)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun editMessage(channelId: String, messageId: String, message: MessageEditPatchRequest) = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(MessageEditPatchRequest.serializer(), message)
    }

    suspend fun putChannel(channelId: String, channel: ChannelModifyPutRequest, reason: String? = null) = call(Route.ChannelPut) {
        keys[Route.ChannelId] = channelId
        body(ChannelModifyPutRequest.serializer(), channel)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }


    suspend fun patchChannel(channelId: String, channel: ChannelModifyPatchRequest, reason: String? = null) = call(Route.ChannelPatch) {
        keys[Route.ChannelId] = channelId
        body(ChannelModifyPatchRequest.serializer(), channel)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

}

