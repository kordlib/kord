package dev.kord.rest.service

import dev.kord.common.entity.*
import dev.kord.rest.*
import dev.kord.rest.builder.channel.*
import dev.kord.rest.builder.channel.thread.StartForumThreadBuilder
import dev.kord.rest.builder.channel.thread.StartThreadBuilder
import dev.kord.rest.builder.channel.thread.StartThreadWithMessageBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.modify.UserMessageModifyBuilder
import dev.kord.rest.json.request.*
import dev.kord.rest.json.response.FollowedChannelResponse
import dev.kord.rest.json.response.ListThreadsResponse
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Position
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class ChannelService(public val client: HttpClient) {

    public suspend fun createMessage(
        channelId: Snowflake,
        multipartRequest: MultipartMessageCreateRequest,
    ): DiscordMessage {
        val form = formData {
            append(FormPart("payload_json", multipartRequest.request))
            multipartRequest.files.forEachIndexed { index, namedFile ->
                append("files[$index]", namedFile.contentProvider)
            }
        }
        return client.post(Routes.Channels.ById(channelId)) {
            setBody(form)
        }.body()
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
    ): List<DiscordMessage> =
        client.get(Routes.Channels.ById(channelId)) {
            position?.let { parameter(it.key, it.value) }
            limit?.let { parameter("limit", it) }
        }.body()

    public suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): DiscordMessage =
        client.get(Routes.Channels.ById.Messages.ById(channelId, messageId)).body()
    public suspend fun getChannelInvites(channelId: Snowflake): List<DiscordInviteWithMetadata> =
        client.get(Routes.Channels.ById.Invites(channelId)).body()

    public suspend fun getChannel(channelId: Snowflake): DiscordChannel =
        client.get(Routes.Channels.ById(channelId)).body()

    public suspend fun addPinnedMessage(channelId: Snowflake, messageId: Snowflake, reason: String? = null) {
        client.put(Routes.Channels.ById.Pins.ById(channelId, messageId)) {
            auditLogReason(reason)
        }
    }

    public suspend fun getChannelPins(channelId: Snowflake): List<DiscordMessage> =
        client.get(Routes.Channels.ById.Pins(channelId)).body()
    public suspend fun createReaction(channelId: Snowflake, messageId: Snowflake, emoji: String) {
        client.put(Routes.Channels.ById.Messages.ById.Reactions.ById(channelId, messageId, emoji))
    }

    public suspend fun deleteReaction(
        channelId: Snowflake,
        messageId: Snowflake,
        userId: Snowflake,
        emoji: String,
    ) {
        client.delete(Routes.Channels.ById.Messages.ById.Reactions.ById.ReactorById(channelId, messageId, emoji, userId))
    }
    public suspend fun deleteOwnReaction(channelId: Snowflake, messageId: Snowflake, emoji: String) {
        client.delete(Routes.Channels.ById.Messages.ById.Reactions.ById.Me(channelId, messageId, emoji))
    }


    public suspend fun deleteAllReactionsForEmoji(channelId: Snowflake, messageId: Snowflake, emoji: String) {
        client.delete(Routes.Channels.ById.Messages.ById.Reactions.ById(channelId, messageId, emoji))
    }

    public suspend fun deletePinnedMessage(channelId: Snowflake, messageId: Snowflake, reason: String? = null) {
        client.delete(Routes.Channels.ById.Pins.ById(channelId, messageId)) {
            auditLogReason(reason)
        }
    }


    public suspend fun deleteAllReactions(channelId: Snowflake, messageId: Snowflake): Unit {
        client.delete(Routes.Channels.ById.Messages.ById.Reactions(channelId, messageId))
    }


    public suspend fun deleteMessage(channelId: Snowflake, messageId: Snowflake, reason: String? = null) {
        client.delete(Routes.Channels.ById.Messages.ById(channelId, messageId)) {
            auditLogReason(reason)
        }
    }


    public suspend fun bulkDelete(channelId: Snowflake, messages: BulkDeleteRequest, reason: String? = null) {
        client.post(Routes.Channels.ById.Messages.BulkDelete(channelId)) {
            setBody(messages)
            auditLogReason(reason)
        }
    }

    public suspend fun deleteChannel(channelId: Snowflake, reason: String? = null): DiscordChannel =
        client.delete(Routes.Channels.ById(channelId)){
            auditLogReason(reason)
        }.body()

    public suspend fun deleteChannelPermission(
        channelId: Snowflake,
        overwriteId: Snowflake,
        reason: String? = null,
    ) {
        client.delete(Routes.Channels.ById.Permissions.ById(channelId, overwriteId)) {
            auditLogReason(reason)
        }
    }
    public suspend fun editChannelPermissions(
        channelId: Snowflake,
        overwriteId: Snowflake,
        permissions: ChannelPermissionEditRequest,
        reason: String? = null,
    ) {
        client.patch(Routes.Channels.ById.Permissions.ById(channelId, overwriteId)) {
            setBody(permissions)
            auditLogReason(reason)
        }
    }
    public suspend fun getReactions(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String,
        after: Position.After? = null,
        limit: Int? = null,
    ): List<DiscordUser> = 
        client.get(Routes.Channels.ById.Messages.ById.Reactions.ById(channelId, messageId, emoji)) {
            after?.let { parameter(it.key, it.value) }
            limit?.let { parameter("limit", it) }
        }.body()
    

    public suspend fun triggerTypingIndicator(channelId: Snowflake) {
        client.post(Routes.Channels.ById.Typing(channelId))
    }

    public suspend fun removeFromGroup(channelId: Snowflake, userId: Snowflake) {
        client.delete(Routes.Channels.ById.Recipients.ById(channelId, userId))
    }

    public suspend fun addToGroup(channelId: Snowflake, userId: Snowflake, addUser: UserAddDMRequest) {
        client.put(Routes.Channels.ById.Recipients.ById(channelId, userId)) {
            setBody(addUser)
        }
    }

    public suspend inline fun createInvite(
        channelId: Snowflake,
        request: InviteCreateRequest,
        reason: String? = null
    ): DiscordInviteWithMetadata {
        return client.post(Routes.Channels.ById.Invites) {
            setBody(request)
            auditLogReason(reason)
        }.body()
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
    ): DiscordMessage =
        client.patch(Routes.Channels.ById.Messages.ById(channelId, messageId)) {
        setBody(request)
    }.body()

    public suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MultipartMessagePatchRequest,
    ): DiscordMessage =
        client.patch(Routes.Channels.ById.Messages.ById(channelId, messageId)) {
        setBody(request)
        // TODO("Files")
    }.body()

    public suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MultipartWebhookEditMessageRequest,
    ): DiscordMessage =
        client.patch(Routes.Channels.ById.Messages.ById(channelId, messageId)) {
            setBody(request.request)
            // TODO("Files")
    }.body()
    public suspend fun putChannel(
        channelId: Snowflake,
        channel: ChannelModifyPutRequest,
        reason: String? = null,
    ): DiscordChannel =
        client.put(Routes.Channels.ById(channelId)) {
        setBody(channel)
        auditLogReason(reason)
    }.body()

    public suspend fun patchChannel(
        channelId: Snowflake,
        channel: ChannelModifyPatchRequest,
        reason: String? = null,
    ): DiscordChannel =
        client.patch(Routes.Channels.ById(channelId)) {
        setBody(channel)
        auditLogReason(reason)
    }.body()

    public suspend fun patchThread(
        threadId: Snowflake,
        thread: ChannelModifyPatchRequest,
        reason: String? = null,
    ): DiscordChannel = patchChannel(threadId, thread, reason)

    public suspend fun crossPost(channelId: Snowflake, messageId: Snowflake): DiscordMessage =
        client.post(Routes.Channels.ById.Messages.ById.CrossPost(channelId, messageId)).body()

    public suspend fun followNewsChannel(channelId: Snowflake, request: ChannelFollowRequest): FollowedChannelResponse =
        client.post(Routes.Channels.ById.Followers(channelId)) {
            setBody(request)
        }.body()

    public suspend fun startThreadWithMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: StartThreadRequest,
        reason: String? = null,
    ): DiscordChannel = client.post(Routes.Channels.ById.Messages.ById.Threads(channelId, messageId)) {
        setBody(request)
        auditLogReason(reason)
    }.body()

    public suspend fun startThreadWithMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        name: String,
        archiveDuration: ArchiveDuration,
        builder: StartThreadWithMessageBuilder.() -> Unit
    ): DiscordChannel {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val startBuilder = StartThreadWithMessageBuilder(name).apply {
            this.autoArchiveDuration = archiveDuration
            builder()
        }
        return startThreadWithMessage(channelId, messageId, startBuilder.toRequest(), startBuilder.reason)
    }

    public suspend fun startThread(
        channelId: Snowflake,
        multipartRequest: MultipartStartThreadRequest,
        reason: String? = null,
    ): DiscordChannel = client.post(Routes.Channels.ById(channelId)) {
        setBody(multipartRequest.request)
        auditLogReason(reason)
        // TODO("Files")
    }.body()

    public suspend fun startThread(
        channelId: Snowflake,
        request: StartThreadRequest,
        reason: String? = null,
    ): DiscordChannel {
        return startThread(channelId, MultipartStartThreadRequest(request), reason)
    }

    public suspend fun startThread(
        channelId: Snowflake,
        name: String,
        archiveDuration: ArchiveDuration,
        type: ChannelType,
        builder: StartThreadBuilder.() -> Unit = {}
    ): DiscordChannel {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val startBuilder = StartThreadBuilder(name, type).apply {
            this.autoArchiveDuration = archiveDuration
            builder()
        }
        return startThread(channelId, startBuilder.toRequest(), startBuilder.reason)
    }

    public suspend fun startForumThread(
        channelId: Snowflake,
        name: String,
        builder: StartForumThreadBuilder.() -> Unit = {}
    ): DiscordChannel {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val startBuilder = StartForumThreadBuilder(name).apply(builder)
        return startThread(channelId, startBuilder.toRequest(), startBuilder.reason)
    }

    public suspend fun joinThread(channelId: Snowflake) {
        client.post(Routes.Channels.ById.ThreadMembers.Me(channelId))
    }

    public suspend fun addUserToThread(channelId: Snowflake, userId: Snowflake) {
        client.post(Routes.Channels.ById.ThreadMembers.ById(channelId, userId))
    }

    public suspend fun leaveThread(channelId: Snowflake) {
        client.delete(Routes.Channels.ById.ThreadMembers.Me(channelId))
    }

    public suspend fun removeUserFromThread(channelId: Snowflake, userId: Snowflake) {
        client.delete(Routes.Channels.ById.ThreadMembers.ById(channelId, userId))
    }

    public suspend fun listThreadMembers(channelId: Snowflake): List<DiscordThreadMember> =
        client.get(Routes.Channels.ById.ThreadMembers(channelId)).body()


    public suspend fun listPublicArchivedThreads(
        channelId: Snowflake,
        request: ListThreadsByTimestampRequest,
    ): ListThreadsResponse =
        client.get(Routes.Channels.ById.Threads.Archived.Public(channelId)) {
            listThreadsConfig(request.before, request.limit)
    }.body()

    public suspend fun listPrivateArchivedThreads(
        channelId: Snowflake,
        request: ListThreadsByTimestampRequest,
    ): ListThreadsResponse = client.get(Routes.Channels.ById.Threads.Archived.Private(channelId)) {
        listThreadsConfig(request.before, request.limit)
    }.body()

    public suspend fun listJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        request: ListThreadsBySnowflakeRequest,
    ): ListThreadsResponse = client.get(Routes.Channels.ById.Users.Me.Threads.Archived.Private(channelId)) {
        listThreadsConfig(request.before, request.limit)
    }.body()
}

private fun HttpRequestBuilder.listThreadsConfig(before: Any?, limit: Int?) {
    if (before != null) parameter("before", before)
    if (limit != null) parameter("limit", limit)
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


public suspend inline fun ChannelService.patchForumChannel(
    channelId: Snowflake,
    builder: ForumChannelModifyBuilder.() -> Unit
): DiscordChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val modifyBuilder = ForumChannelModifyBuilder().apply(builder)
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
