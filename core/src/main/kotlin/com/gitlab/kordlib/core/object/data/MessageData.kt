package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal val MessageData.authorId get() = author?.id

@Serializable
data class MessageData(
        val id: String,
        var channelId: String,
        var guildId: String? = null,
        var author: User?,
        var content: String,
        var timestamp: String,
        var editedTimestamp: String? = null,
        var tts: Boolean,
        var mentionEveryone: Boolean,
        var mentions: List<String>,
        var mentionRoles: List<String>,
        var attachments: List<AttachmentData>,
        var embeds: List<EmbedData>,
        var reactions: List<Reaction>? = null,
        var nonce: String? = null,
        var pinned: Boolean,
        var webhookId: String?,
        var type: MessageType,
        var activity: MessageActivity? = null,
        var application: MessageApplication? = null
) {
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
                    reactions,
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
