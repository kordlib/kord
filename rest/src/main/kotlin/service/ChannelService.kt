package dev.kord.rest.service

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.builder.channel.*
import dev.kord.rest.builder.channel.thread.StartThreadBuilder
import dev.kord.rest.builder.channel.thread.StartThreadWithMessageBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.modify.UserMessageModifyBuilder
import dev.kord.rest.json.request.*
import dev.kord.rest.json.response.FollowedChannelResponse
import dev.kord.rest.json.response.ListThreadsResponse
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Position
import dev.kord.rest.route.Route
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ChannelService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun createMessage(channelId: Snowflake, multipartRequest: MultipartMessageCreateRequest): DiscordMessage {

        return call(Route.MessagePost) {
            keys[Route.ChannelId] = channelId
            body(MessageCreateRequest.serializer(), multipartRequest.request)
            multipartRequest.files.forEach { file(it) }
        }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createMessage(channelId: Snowflake, builder: UserMessageCreateBuilder.() -> Unit): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val multipartRequest = UserMessageCreateBuilder().apply(builder).toRequest()
        return createMessage(channelId, multipartRequest)
    }

    suspend fun getMessages(channelId: Snowflake, position: Position? = null, limit: Int = 50) =
        call(Route.MessagesGet) {
            keys[Route.ChannelId] = channelId
            if (position != null) {
                parameter(position.key, position.value)
            }
            parameter("limit", "$limit")

        }

    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake) = call(Route.MessageGet) {
        keys[Route.MessageId] = messageId
        keys[Route.ChannelId] = channelId
    }

    suspend fun getChannelInvites(channelId: Snowflake) = call(Route.InvitesGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun getChannel(channelId: Snowflake) = call(Route.ChannelGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun addPinnedMessage(channelId: Snowflake, messageId: Snowflake, reason: String?) = call(Route.PinPut) {
        keys[Route.MessageId] = messageId
        keys[Route.ChannelId] = channelId
        auditLogReason(reason)
    }

    suspend fun getChannelPins(channelId: Snowflake) = call(Route.PinsGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun createReaction(channelId: Snowflake, messageId: Snowflake, emoji: String) = call(Route.ReactionPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji
    }

    suspend fun deleteReaction(channelId: Snowflake, messageId: Snowflake, userId: Snowflake, emoji: String) =
        call(Route.ReactionDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            keys[Route.Emoji] = emoji
            keys[Route.UserId] = userId
        }

    suspend fun deleteOwnReaction(channelId: Snowflake, messageId: Snowflake, emoji: String) =
        call(Route.OwnReactionDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            keys[Route.Emoji] = emoji
        }

    suspend fun deleteAllReactionsForEmoji(channelId: Snowflake, messageId: Snowflake, emoji: String) =
        call(Route.DeleteAllReactionsForEmoji) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            keys[Route.Emoji] = emoji
        }

    suspend fun deletePinnedMessage(channelId: Snowflake, messageId: Snowflake, reason: String? = null) =
        call(Route.PinDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            auditLogReason(reason)
        }

    suspend fun deleteAllReactions(channelId: Snowflake, messageId: Snowflake) = call(Route.AllReactionsDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
    }

    suspend fun deleteMessage(channelId: Snowflake, messageId: Snowflake, reason: String? = null) =
        call(Route.MessageDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            auditLogReason(reason)
        }

    suspend fun bulkDelete(channelId: Snowflake, messages: BulkDeleteRequest, reason: String?) = call(Route.BulkMessageDeletePost) {
        keys[Route.ChannelId] = channelId
        body(BulkDeleteRequest.serializer(), messages)
        auditLogReason(reason)
    }

    suspend fun deleteChannel(channelId: Snowflake, reason: String? = null) = call(Route.ChannelDelete) {
        keys[Route.ChannelId] = channelId
        auditLogReason(reason)
    }

    suspend fun deleteChannelPermission(channelId: Snowflake, overwriteId: Snowflake, reason: String? = null) =
        call(Route.ChannelPermissionDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.OverwriteId] = overwriteId
            auditLogReason(reason)
        }

    suspend fun editChannelPermissions(
        channelId: Snowflake,
        overwriteId: Snowflake,
        permissions: ChannelPermissionEditRequest,
        reason: String? = null
    ) = call(Route.ChannelPermissionPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        body(ChannelPermissionEditRequest.serializer(), permissions)
        auditLogReason(reason)
    }

    suspend fun getReactions(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String,
        position: Position? = null,
        limit: Int = 25
    ) = call(Route.ReactionsGet) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji

        if (position != null) {
            parameter(position.key, position.value)
        }
        parameter("limit", "$limit")
    }

    suspend fun triggerTypingIndicator(channelId: Snowflake) = call(Route.TypingIndicatorPost) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun removeFromGroup(channelId: Snowflake, userId: Snowflake) = call(Route.GroupDMUserDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
    }

    suspend fun addToGroup(channelId: Snowflake, userId: Snowflake, addUser: UserAddDMRequest) =
        call(Route.GroupDMUserPut) {
            keys[Route.ChannelId] = channelId
            keys[Route.UserId] = userId
            body(UserAddDMRequest.serializer(), addUser)
        }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createInvite(channelId: Snowflake, builder: InviteCreateBuilder.() -> Unit = {}): DiscordInvite {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return call(Route.InvitePost) {
            keys[Route.ChannelId] = channelId
            val request = InviteCreateBuilder().apply(builder)
            body(InviteCreateRequest.serializer(), request.toRequest())
            auditLogReason(request.reason)
        }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        builder: UserMessageModifyBuilder.() -> Unit
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return editMessage(channelId, messageId, UserMessageModifyBuilder().apply(builder).toRequest())
    }

    suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MessageEditPatchRequest
    ): DiscordMessage {
        return call(Route.EditMessagePatch) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            body(MessageEditPatchRequest.serializer(), request)
        }
    }

    suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MultipartMessagePatchRequest
    ): DiscordMessage {
        return call(Route.EditMessagePatch) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            body(MessageEditPatchRequest.serializer(), request.requests)

            request.files.orEmpty().forEach { file(it) }
        }
    }

    suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MultipartWebhookEditMessageRequest
    ): DiscordMessage {
        return call(Route.EditMessagePatch) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            body(WebhookEditMessageRequest.serializer(), request.request)

            request.files.orEmpty().forEach { file(it) }
        }
    }



    suspend fun putChannel(channelId: Snowflake, channel: ChannelModifyPutRequest, reason: String? = null) =
        call(Route.ChannelPut) {
            keys[Route.ChannelId] = channelId
            body(ChannelModifyPutRequest.serializer(), channel)
            reason?.let { header("X-Audit-Log-Reason", reason) }
        }

    suspend fun patchChannel(channelId: Snowflake, channel: ChannelModifyPatchRequest, reason: String? = null) =
        call(Route.ChannelPatch) {
            keys[Route.ChannelId] = channelId
            body(ChannelModifyPatchRequest.serializer(), channel)
            auditLogReason(reason)
        }
    suspend fun patchThread(threadId: Snowflake, thread: ChannelModifyPatchRequest, reason: String? = null) =
        call(Route.ChannelPatch) {
            keys[Route.ChannelId] = threadId
            body(ChannelModifyPatchRequest.serializer(), thread)
            reason?.let { header("X-Audit-Log-Reason", reason) }
        }


    suspend fun crossPost(channelId: Snowflake, messageId: Snowflake): DiscordMessage = call(Route.MessageCrosspost) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
    }


    suspend fun followNewsChannel(channelId: Snowflake, request: ChannelFollowRequest): FollowedChannelResponse =
        call(Route.NewsChannelFollow) {
            keys[Route.ChannelId] = channelId
            body(ChannelFollowRequest.serializer(), request)
        }

    suspend fun startThreadWithMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: StartThreadRequest,
        reason: String? = null
    ): DiscordChannel {
        return call(Route.StartPublicThreadWithMessagePost) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            body(StartThreadRequest.serializer(), request)
            reason?.let { header("X-Audit-Log-Reason", reason) }
        }
    }

    @OptIn(ExperimentalContracts::class)
    suspend fun startThreadWithMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        name: String,
        archiveDuration: ArchiveDuration,
        builder: StartThreadWithMessageBuilder.() -> Unit
    ): DiscordChannel {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val startBuilder = StartThreadWithMessageBuilder(name, archiveDuration).apply(builder)
        return startThreadWithMessage(channelId, messageId, startBuilder.toRequest(), startBuilder.reason)
    }

    suspend fun startThread(
        channelId: Snowflake,
        request: StartThreadRequest,
        reason: String? = null
    ): DiscordChannel {
        return call(Route.StartThreadPost) {
            keys[Route.ChannelId] = channelId
            body(StartThreadRequest.serializer(), request)
            reason?.let { header("X-Audit-Log-Reason", reason) }
        }
    }

    @OptIn(ExperimentalContracts::class)
    suspend fun startThread(
        channelId: Snowflake,
        name: String,
        archiveDuration: ArchiveDuration,
        type: ChannelType,
        builder: StartThreadBuilder.() -> Unit = {}
    ): DiscordChannel {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val startBuilder = StartThreadBuilder(name, archiveDuration, type).apply(builder)
        return startThread(channelId, startBuilder.toRequest(), startBuilder.reason)
    }

    suspend fun joinThread(channelId: Snowflake) {
        call(Route.JoinThreadPut) {
            keys[Route.ChannelId] = channelId
        }
    }

    suspend fun addUserToThread(channelId: Snowflake, userId: Snowflake) {
        call(Route.AddThreadMemberPut) {
            keys[Route.ChannelId] = channelId
            keys[Route.UserId] = userId
        }
    }
    suspend fun leaveThread(channelId: Snowflake) {
        call(Route.LeaveThreadDelete) {
            keys[Route.ChannelId] = channelId
        }
    }

    suspend fun removeUserFromThread(channelId: Snowflake, userId: Snowflake) {
        call(Route.RemoveUserFromThreadDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.UserId] = userId
        }
    }

    suspend fun listThreadMembers(channelId: Snowflake): List<DiscordThreadMember> {
        return call(Route.ThreadMembersGet) {
            keys[Route.ChannelId] = channelId
        }
    }


    suspend fun listPublicArchivedThreads(channelId: Snowflake, request: ListThreadsByTimestampRequest): ListThreadsResponse {
        return call(Route.PublicArchivedThreadsGet) {
            keys[Route.ChannelId] = channelId
            val before = request.before
            val limit = request.limit
            if(before != null) parameter("before", before)
            if(limit != null) parameter("limit", limit)

        }
    }

    suspend fun listPrivateArchivedThreads(channelId: Snowflake, request: ListThreadsByTimestampRequest): ListThreadsResponse {
        return call(Route.PrivateArchivedThreadsGet) {
            keys[Route.ChannelId] = channelId
            val before = request.before
            val limit = request.limit
            if(before != null) parameter("before", before)
            if(limit != null) parameter("limit", limit)

        }
    }

    suspend fun listJoinedPrivateArchivedThreads(channelId: Snowflake, request: ListThreadsBySnowflakeRequest): ListThreadsResponse {
        return call(Route.JoinedPrivateArchivedThreadsGet) {
            keys[Route.ChannelId] = channelId
            val before = request.before
            val limit = request.limit
            if(before != null) parameter("before", before)
            if(limit != null) parameter("limit", limit)

        }

    }

}

@OptIn(ExperimentalContracts::class)
suspend inline fun ChannelService.patchTextChannel(
    channelId: Snowflake,
    builder: TextChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = TextChannelModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun ChannelService.patchVoiceChannel(
    channelId: Snowflake,
    builder: VoiceChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = VoiceChannelModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}


@OptIn(ExperimentalContracts::class)
suspend inline fun ChannelService.patchStageVoiceChannel(
    channelId: Snowflake,
    builder: StageVoiceChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return patchChannel(channelId, StageVoiceChannelModifyBuilder().apply(builder).toRequest())
}

@OptIn(ExperimentalContracts::class)
suspend inline fun ChannelService.patchStoreChannel(
    channelId: Snowflake,
    builder: StoreChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = StoreChannelModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun ChannelService.patchNewsChannel(
    channelId: Snowflake,
    builder: NewsChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = NewsChannelModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun ChannelService.patchCategory(
    channelId: Snowflake,
    builder: CategoryModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = CategoryModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun ChannelService.editMemberPermissions(
    channelId: Snowflake,
    memberId: Snowflake,
    builder: ChannelPermissionModifyBuilder.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = ChannelPermissionModifyBuilder(OverwriteType.Member).apply(builder)
    editChannelPermissions(channelId, memberId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun ChannelService.editRolePermission(
    channelId: Snowflake,
    roleId: Snowflake,
    builder: ChannelPermissionModifyBuilder.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = ChannelPermissionModifyBuilder(OverwriteType.Role).apply(builder)
    editChannelPermissions(channelId, roleId, modifyBuilder.toRequest(), modifyBuilder.reason)
}
