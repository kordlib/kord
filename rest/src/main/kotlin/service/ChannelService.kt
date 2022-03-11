package dev.kord.rest.service

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
import kotlin.DeprecationLevel.WARNING
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class ChannelService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun createMessage(
        channelId: Snowflake,
        multipartRequest: MultipartMessageCreateRequest,
    ): DiscordMessage = call(Route.MessagePost) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), multipartRequest.request)
        multipartRequest.files.forEach { file(it) }
    }

    public suspend inline fun createMessage(
        channelId: Snowflake,
        builder: UserMessageCreateBuilder.() -> Unit,
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val multipartRequest = UserMessageCreateBuilder().apply(builder).toRequest()
        return createMessage(channelId, multipartRequest)
    }

    public suspend fun getMessages(
        channelId: Snowflake,
        position: Position? = null,
        limit: Int? = null,
    ): List<DiscordMessage> = call(Route.MessagesGet) {
        keys[Route.ChannelId] = channelId
        position?.let { parameter(it.key, it.value) }
        limit?.let { parameter("limit", it) }
    }

    public suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): DiscordMessage = call(Route.MessageGet) {
        keys[Route.MessageId] = messageId
        keys[Route.ChannelId] = channelId
    }

    public suspend fun getChannelInvites(channelId: Snowflake): List<DiscordInviteWithMetadata> =
        call(Route.InvitesGet) {
            keys[Route.ChannelId] = channelId
        }

    public suspend fun getChannel(channelId: Snowflake): DiscordChannel = call(Route.ChannelGet) {
        keys[Route.ChannelId] = channelId
    }

    public suspend fun addPinnedMessage(channelId: Snowflake, messageId: Snowflake, reason: String? = null): Unit =
        call(Route.PinPut) {
            keys[Route.MessageId] = messageId
            keys[Route.ChannelId] = channelId
            auditLogReason(reason)
        }

    public suspend fun getChannelPins(channelId: Snowflake): List<DiscordMessage> = call(Route.PinsGet) {
        keys[Route.ChannelId] = channelId
    }

    public suspend fun createReaction(channelId: Snowflake, messageId: Snowflake, emoji: String): Unit =
        call(Route.ReactionPut) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            keys[Route.Emoji] = emoji
        }

    public suspend fun deleteReaction(
        channelId: Snowflake,
        messageId: Snowflake,
        userId: Snowflake,
        emoji: String,
    ): Unit = call(Route.ReactionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji
        keys[Route.UserId] = userId
    }

    public suspend fun deleteOwnReaction(channelId: Snowflake, messageId: Snowflake, emoji: String): Unit =
        call(Route.OwnReactionDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            keys[Route.Emoji] = emoji
        }

    public suspend fun deleteAllReactionsForEmoji(channelId: Snowflake, messageId: Snowflake, emoji: String): Unit =
        call(Route.DeleteAllReactionsForEmoji) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            keys[Route.Emoji] = emoji
        }

    public suspend fun deletePinnedMessage(channelId: Snowflake, messageId: Snowflake, reason: String? = null): Unit =
        call(Route.PinDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            auditLogReason(reason)
        }

    public suspend fun deleteAllReactions(channelId: Snowflake, messageId: Snowflake): Unit =
        call(Route.AllReactionsDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
        }

    public suspend fun deleteMessage(channelId: Snowflake, messageId: Snowflake, reason: String? = null): Unit =
        call(Route.MessageDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
            auditLogReason(reason)
        }

    public suspend fun bulkDelete(channelId: Snowflake, messages: BulkDeleteRequest, reason: String? = null): Unit =
        call(Route.BulkMessageDeletePost) {
            keys[Route.ChannelId] = channelId
            body(BulkDeleteRequest.serializer(), messages)
            auditLogReason(reason)
        }

    public suspend fun deleteChannel(channelId: Snowflake, reason: String? = null): DiscordChannel =
        call(Route.ChannelDelete) {
            keys[Route.ChannelId] = channelId
            auditLogReason(reason)
        }

    public suspend fun deleteChannelPermission(
        channelId: Snowflake,
        overwriteId: Snowflake,
        reason: String? = null,
    ): Unit = call(Route.ChannelPermissionDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        auditLogReason(reason)
    }

    public suspend fun editChannelPermissions(
        channelId: Snowflake,
        overwriteId: Snowflake,
        permissions: ChannelPermissionEditRequest,
        reason: String? = null,
    ): Unit = call(Route.ChannelPermissionPut) {
        keys[Route.ChannelId] = channelId
        keys[Route.OverwriteId] = overwriteId
        body(ChannelPermissionEditRequest.serializer(), permissions)
        auditLogReason(reason)
    }

    public suspend fun getReactions(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String,
        after: Position.After? = null,
        limit: Int? = null,
    ): List<DiscordUser> = call(Route.ReactionsGet) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.Emoji] = emoji

        after?.let { parameter(it.key, it.value) }
        limit?.let { parameter("limit", it) }
    }

    public suspend fun triggerTypingIndicator(channelId: Snowflake): Unit = call(Route.TypingIndicatorPost) {
        keys[Route.ChannelId] = channelId
    }

    public suspend fun removeFromGroup(channelId: Snowflake, userId: Snowflake): Unit = call(Route.GroupDMUserDelete) {
        keys[Route.ChannelId] = channelId
        keys[Route.UserId] = userId
    }

    public suspend fun addToGroup(channelId: Snowflake, userId: Snowflake, addUser: UserAddDMRequest): Unit =
        call(Route.GroupDMUserPut) {
            keys[Route.ChannelId] = channelId
            keys[Route.UserId] = userId
            body(UserAddDMRequest.serializer(), addUser)
        }

    public suspend inline fun createInvite(
        channelId: Snowflake,
        builder: InviteCreateBuilder.() -> Unit = {},
    ): DiscordInviteWithMetadata {
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

    public suspend inline fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        builder: UserMessageModifyBuilder.() -> Unit
    ): DiscordMessage {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return editMessage(channelId, messageId, UserMessageModifyBuilder().apply(builder).toRequest())
    }

    public suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MessageEditPatchRequest,
    ): DiscordMessage = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(MessageEditPatchRequest.serializer(), request)
    }

    public suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MultipartMessagePatchRequest,
    ): DiscordMessage = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(MessageEditPatchRequest.serializer(), request.requests)

        request.files.orEmpty().forEach { file(it) }
    }

    public suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MultipartWebhookEditMessageRequest,
    ): DiscordMessage = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(WebhookEditMessageRequest.serializer(), request.request)

        request.files.orEmpty().forEach { file(it) }
    }

    public suspend fun putChannel(
        channelId: Snowflake,
        channel: ChannelModifyPutRequest,
        reason: String? = null,
    ): DiscordChannel = call(Route.ChannelPut) {
        keys[Route.ChannelId] = channelId
        body(ChannelModifyPutRequest.serializer(), channel)
        auditLogReason(reason)
    }

    public suspend fun patchChannel(
        channelId: Snowflake,
        channel: ChannelModifyPatchRequest,
        reason: String? = null,
    ): DiscordChannel = call(Route.ChannelPatch) {
        keys[Route.ChannelId] = channelId
        body(ChannelModifyPatchRequest.serializer(), channel)
        auditLogReason(reason)
    }

    public suspend fun patchThread(
        threadId: Snowflake,
        thread: ChannelModifyPatchRequest,
        reason: String? = null,
    ): DiscordChannel = call(Route.ChannelPatch) {
        keys[Route.ChannelId] = threadId
        body(ChannelModifyPatchRequest.serializer(), thread)
        auditLogReason(reason)
    }

    public suspend fun crossPost(channelId: Snowflake, messageId: Snowflake): DiscordMessage =
        call(Route.MessageCrosspost) {
            keys[Route.ChannelId] = channelId
            keys[Route.MessageId] = messageId
        }

    public suspend fun followNewsChannel(channelId: Snowflake, request: ChannelFollowRequest): FollowedChannelResponse =
        call(Route.NewsChannelFollow) {
            keys[Route.ChannelId] = channelId
            body(ChannelFollowRequest.serializer(), request)
        }

    public suspend fun startThreadWithMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: StartThreadRequest,
        reason: String? = null,
    ): DiscordChannel = call(Route.StartPublicThreadWithMessagePost) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(StartThreadRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend fun startThreadWithMessage(
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

    public suspend fun startThread(
        channelId: Snowflake,
        request: StartThreadRequest,
        reason: String? = null,
    ): DiscordChannel = call(Route.StartThreadPost) {
        keys[Route.ChannelId] = channelId
        body(StartThreadRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend fun startThread(
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

    public suspend fun joinThread(channelId: Snowflake) {
        call(Route.JoinThreadPut) {
            keys[Route.ChannelId] = channelId
        }
    }

    public suspend fun addUserToThread(channelId: Snowflake, userId: Snowflake) {
        call(Route.AddThreadMemberPut) {
            keys[Route.ChannelId] = channelId
            keys[Route.UserId] = userId
        }
    }

    public suspend fun leaveThread(channelId: Snowflake) {
        call(Route.LeaveThreadDelete) {
            keys[Route.ChannelId] = channelId
        }
    }

    public suspend fun removeUserFromThread(channelId: Snowflake, userId: Snowflake) {
        call(Route.RemoveUserFromThreadDelete) {
            keys[Route.ChannelId] = channelId
            keys[Route.UserId] = userId
        }
    }

    public suspend fun listThreadMembers(channelId: Snowflake): List<DiscordThreadMember> =
        call(Route.ThreadMembersGet) {
            keys[Route.ChannelId] = channelId
        }

    public suspend fun listPublicArchivedThreads(
        channelId: Snowflake,
        request: ListThreadsByTimestampRequest,
    ): ListThreadsResponse = call(Route.PublicArchivedThreadsGet) {
        keys[Route.ChannelId] = channelId
        val before = request.before
        val limit = request.limit
        if (before != null) parameter("before", before)
        if (limit != null) parameter("limit", limit)
    }

    public suspend fun listPrivateArchivedThreads(
        channelId: Snowflake,
        request: ListThreadsByTimestampRequest,
    ): ListThreadsResponse = call(Route.PrivateArchivedThreadsGet) {
        keys[Route.ChannelId] = channelId
        val before = request.before
        val limit = request.limit
        if (before != null) parameter("before", before)
        if (limit != null) parameter("limit", limit)
    }

    public suspend fun listJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        request: ListThreadsBySnowflakeRequest,
    ): ListThreadsResponse = call(Route.JoinedPrivateArchivedThreadsGet) {
        keys[Route.ChannelId] = channelId
        val before = request.before
        val limit = request.limit
        if (before != null) parameter("before", before)
        if (limit != null) parameter("limit", limit)
    }
}

public suspend inline fun ChannelService.patchTextChannel(
    channelId: Snowflake,
    builder: TextChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = TextChannelModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

public suspend inline fun ChannelService.patchVoiceChannel(
    channelId: Snowflake,
    builder: VoiceChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = VoiceChannelModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

public suspend inline fun ChannelService.patchStageVoiceChannel(
    channelId: Snowflake,
    builder: StageVoiceChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return patchChannel(channelId, StageVoiceChannelModifyBuilder().apply(builder).toRequest())
}

@Suppress("DEPRECATION")
@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
    """,
    level = WARNING,
)
public suspend inline fun ChannelService.patchStoreChannel(
    channelId: Snowflake,
    builder: StoreChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = StoreChannelModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

public suspend inline fun ChannelService.patchNewsChannel(
    channelId: Snowflake,
    builder: NewsChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = NewsChannelModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

public suspend inline fun ChannelService.patchCategory(
    channelId: Snowflake,
    builder: CategoryModifyBuilder.() -> Unit
): DiscordChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val modifyBuilder = CategoryModifyBuilder().apply(builder)
    return patchChannel(channelId, modifyBuilder.toRequest(), modifyBuilder.reason)
}

public suspend inline fun ChannelService.editMemberPermissions(
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

public suspend inline fun ChannelService.editRolePermission(
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
