package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.builder.channel.*
import com.gitlab.kordlib.rest.builder.message.MessageCreateBuilder
import com.gitlab.kordlib.rest.builder.message.MessageModifyBuilder
import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Position
import com.gitlab.kordlib.rest.route.Route
import java.awt.Color
import java.time.Instant
import java.time.format.DateTimeFormatter

class ChannelService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend inline fun createMessage(channelId: String, builder: MessageCreateBuilder.() -> Unit) = call(Route.MessagePost) {
        keys[Route.ChannelId] = channelId
        val multipartRequest = MessageCreateBuilder().apply(builder).toRequest()
        body(MessageCreateRequest.serializer(), multipartRequest.request)
        multipartRequest.files.forEach { file(it) }
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

    suspend fun deleteAllReactionsForEmoji(channelId: String, messageId: String, emojiName: String, emojiId: String) = call(Route.DeleteAllReactionsForEmoji) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emojiName
        keys[Route.EmojiId] = emojiId
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
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun bulkDelete(channelId: String, messages: BulkDeleteRequest) = call(Route.BulkMessageDeletePost) {
        keys[Route.ChannelId] = channelId
        body(BulkDeleteRequest.serializer(), messages)
    }

    suspend fun deleteChannel(channelId: String, reason: String? = null) = call(Route.ChannelDelete) {
        keys[Route.ChannelId] = channelId
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun deleteChannelPermission(channelId: String, overwriteId: String, reason: String? = null) = call(Route.ChannelPermissionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun editChannelPermissions(channelId: String, overwriteId: String, permissions: ChannelPermissionEditRequest, reason: String? = null) = call(Route.ChannelPermissionPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        body(ChannelPermissionEditRequest.serializer(), permissions)
        reason?.let { header("X-Audit-Log-Reason", reason) }
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

    suspend fun addToGroup(channelId: String, userId: String, addUser: UserAddDMRequest) = call(Route.GroupDMUserPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
        body(UserAddDMRequest.serializer(), addUser)
    }

    suspend inline fun createInvite(channelId: String, builder: InviteCreateBuilder.() -> Unit = {}) = call(Route.InvitePost) {
        keys[Route.ChannelId] = channelId
        val request = InviteCreateBuilder().apply(builder)
        body(InviteCreateRequest.serializer(), request.toRequest())
        request.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend inline fun editMessage(channelId: String, messageId: String, builder: MessageModifyBuilder.() -> Unit) = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(MessageEditPatchRequest.serializer(), MessageModifyBuilder().apply(builder).toRequest())
    }


    suspend fun putChannel(channelId: String, channel: ChannelModifyPutRequest, reason: String? = null) = call(Route.ChannelPut) {
        keys[Route.ChannelId] = channelId
        body(ChannelModifyPutRequest.serializer(), channel)
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun patchChannel(channelId: String, channel: ChannelModifyPatchRequest, reason: String? = null) = call(Route.ChannelPatch) {
        keys[Route.ChannelId] = channelId
        body(ChannelModifyPatchRequest.serializer(), channel)
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

}

suspend inline fun ChannelService.patchTextChannel(channelId: String, builder: TextChannelModifyBuilder.() -> Unit) =
        patchChannel(channelId, TextChannelModifyBuilder().apply(builder).toRequest())

suspend inline fun ChannelService.patchVoiceChannel(channelId: String, builder: VoiceChannelModifyBuilder.() -> Unit) =
        patchChannel(channelId, VoiceChannelModifyBuilder().apply(builder).toRequest())

suspend inline fun ChannelService.patchStoreChannel(channelId: String, builder: StoreChannelModifyBuilder.() -> Unit) =
        patchChannel(channelId, StoreChannelModifyBuilder().apply(builder).toRequest())

suspend inline fun ChannelService.patchNewsChannel(channelId: String, builder: NewsChannelModifyBuilder.() -> Unit) =
        patchChannel(channelId, NewsChannelModifyBuilder().apply(builder).toRequest())

suspend inline fun ChannelService.patchCategory(channelId: String, builder: CategoryModifyBuilder.() -> Unit) =
        patchChannel(channelId, CategoryModifyBuilder().apply(builder).toRequest())

suspend inline fun ChannelService.editMemberPermissions(channelId: String, memberId: String, builder: ChannelPermissionModifyBuilder.() -> Unit) {
    val modifyBuilder = ChannelPermissionModifyBuilder("member").apply(builder)
    editChannelPermissions(channelId, memberId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

suspend inline fun ChannelService.editRolePermission(channelId: String, roleId: String, builder: ChannelPermissionModifyBuilder.() -> Unit) {
    val modifyBuilder = ChannelPermissionModifyBuilder("role").apply(builder)
    editChannelPermissions(channelId, roleId, modifyBuilder.toRequest(), modifyBuilder.reason)
}
