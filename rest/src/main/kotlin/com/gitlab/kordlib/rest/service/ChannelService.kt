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

    @Deprecated("Use the inline builder instead.", ReplaceWith("""
        createMessage(channelId) { 
            val oldMessage = message.request
            content = oldMessage.content
            nonce = oldMessage.nonce
            tts = oldMessage.tts
            
            message.files.forEach { (name, input) ->
                addFile(name, input)
            }
            
            embed {
                title = oldMessage.embed?.title
                description = oldMessage.embed?.description
                url = oldMessage.embed?.url
                timestamp = oldMessage.embed?.timestamp?.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(it, Instant::from) } 
                color = oldMessage.embed?.color?.let { Color(it) }
                
                oldMessage.embed?.footer?.let {
                    footer {
                        text = it.text
                        url = it.url
                        icon = it.iconUrl
                    }
                }
                
                image = oldMessage.embed?.image?.url
                oldMessage.embed?.thumbnail?.url?.let {
                    thumbnail {
                        url = it
                    }
                }
                
                oldMessage.embed?.author?.let {
                    author {
                        name = it.name
                        url = it.url
                        icon = it.iconUrl
                    }
                }
                
                oldMessage.embed?.fields?.forEach {
                    field {
                        name = it.name
                        value = it.value
                        inline = it.inline ?: false
                    }
                }
            }
        }
    """, "java.time.format.DateTimeFormatter", "java.awt.Color", "java.time.Instant"), DeprecationLevel.ERROR)
    suspend fun createMessage(channelId: String, message: MultipartMessageCreateRequest) = call(Route.MessagePost) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), message.request)
        message.files.forEach { file(it) }
    }

    @Deprecated("Will be removed in 0.5.0. Use the inline builder instead.", ReplaceWith("""
        createMessage(channelId) { 
            val oldMessage = message
            content = oldMessage.content
            nonce = oldMessage.nonce
            tts = oldMessage.tts
            
            embed {
                title = oldMessage.embed?.title
                description = oldMessage.embed?.description
                url = oldMessage.embed?.url
                timestamp = oldMessage.embed?.timestamp?.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(it, Instant::from) } 
                color = oldMessage.embed?.color?.let { Color(it) }
                
                oldMessage.embed?.footer?.let {
                    footer {
                        text = it.text
                        url = it.url
                        icon = it.iconUrl
                    }
                }
                
                image = oldMessage.embed?.image?.url
                oldMessage.embed?.thumbnail?.url?.let {
                    thumbnail {
                        url = it
                    }
                }
                
                oldMessage.embed?.author?.let {
                    author {
                        name = it.name
                        url = it.url
                        icon = it.iconUrl
                    }
                }
                
                oldMessage.embed?.fields?.forEach {
                    field {
                        name = it.name
                        value = it.value
                        inline = it.inline ?: false
                    }
                }
            }
        }
    """, "java.time.format.DateTimeFormatter", "java.awt.Color", "java.time.Instant"), DeprecationLevel.ERROR)
    suspend fun createMessage(channelId: String, message: MessageCreateRequest) = call(Route.MessagePost) {
        keys[Route.ChannelId] = channelId
        body(MessageCreateRequest.serializer(), message)
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

    @Deprecated("Will be removed in 0.5.0, use the inline builder instead", ReplaceWith("createInvite(channelId) { this@createInvite.reason = reason }"), DeprecationLevel.ERROR)
    suspend fun createInvite(channelId: String, invite: InviteCreateRequest, reason: String? = null) = call(Route.InvitePost) {
        keys[Route.ChannelId] = channelId
        body(InviteCreateRequest.serializer(), invite)
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend inline fun editMessage(channelId: String, messageId: String, builder: MessageModifyBuilder.() -> Unit) = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(MessageEditPatchRequest.serializer(), MessageModifyBuilder().apply(builder).toRequest())
    }


    @Deprecated("Will be removed in 0.5.0, use the inline builder instead", ReplaceWith("""
        editMessage(channelId, messageId) { 
            val oldMessage = message
            content = oldMessage.content
            
            embed {
                title = oldMessage.embed?.title
                description = oldMessage.embed?.description
                url = oldMessage.embed?.url
                timestamp = oldMessage.embed?.timestamp?.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(it, Instant::from) } 
                color = oldMessage.embed?.color?.let { Color(it) }
                
                oldMessage.embed?.footer?.let {
                    footer {
                        text = it.text
                        url = it.url
                        icon = it.iconUrl
                    }
                }
                
                image = oldMessage.embed?.image?.url
                oldMessage.embed?.thumbnail?.url?.let {
                    thumbnail {
                        url = it
                    }
                }
                
                oldMessage.embed?.author?.let {
                    author {
                        name = it.name
                        url = it.url
                        icon = it.iconUrl
                    }
                }
                
                oldMessage.embed?.fields?.forEach {
                    field {
                        name = it.name
                        value = it.value
                        inline = it.inline ?: false
                    }
                }
            }
        }
    """, "java.time.format.DateTimeFormatter", "java.awt.Color", "java.time.Instant"), DeprecationLevel.ERROR)
    suspend fun editMessage(channelId: String, messageId: String, message: MessageEditPatchRequest) = call(Route.EditMessagePatch) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        body(MessageEditPatchRequest.serializer(), message)
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
