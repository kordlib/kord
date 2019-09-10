package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonException
import kotlinx.serialization.json.content

internal val MessageData.authorId get() = author?.id

@Serializable
data class MessageData(
        val id: String,
        val channelId: String,
        val guildId: String? = null,
        val author: User?,
        val content: String,
        val timestamp: String,
        val editedTimestamp: String? = null,
        val tts: Boolean,
        val mentionEveryone: Boolean,
        val mentions: List<String>,
        val mentionRoles: List<String>,
        val attachments: List<AttachmentData>,
        val embeds: List<EmbedData>,
        val reactions: List<ReactionData>? = null,
        val nonce: String? = null,
        val pinned: Boolean,
        val webhookId: String?,
        val type: MessageType,
        val activity: MessageActivity? = null,
        val application: MessageApplication? = null
) {

    operator fun plus(partialMessage: PartialMessage): MessageData {

        val editedTimestamp = partialMessage.editedTimestamp ?: editedTimestamp
        val content =  partialMessage.content ?: content
        val mentions = partialMessage.mentions.map { it.id }
        val mentionEveryone =  partialMessage.mentionEveryone?: mentionEveryone
        val embeds =  partialMessage.embeds?.map { EmbedData.from(it) } ?: embeds

        return MessageData(
                id,
                channelId,
                guildId,
                author,
                content,
                timestamp,
                editedTimestamp,
                tts,
                mentionEveryone,
                mentions,
                mentionRoles,
                attachments,
                embeds,
                reactions,
                nonce,
                pinned,
                webhookId,
                type,
                activity,
                application
        )
    }

    companion object {
        val description get() = description(MessageData::id)

        fun from(entity: Message) = with(entity) {
            MessageData(
                    id,
                    channelId,
                    guildId,
                    author,
                    content,
                    timestamp,
                    editedTimestamp,
                    tts,
                    mentionEveryone,
                    mentions.map { it.id },
                    mentionRoles.map { it.id },
                    attachments.map { AttachmentData.from(it) },
                    embeds.map { EmbedData.from(it) },
                    reactions?.map { ReactionData.from(it) },
                    nonce,
                    pinned,
                    webhookId,
                    type,
                    activity,
                    application
            )
        }
    }
}
