package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.serialization.Serializable

internal val MessageData.authorId get() = author?.id

@Serializable
data class MessageData(
        val id: Long,
        val channelId: Long,
        val guildId: Long? = null,
        val author: UserData?,
        val content: String,
        val timestamp: String,
        val editedTimestamp: String? = null,
        val tts: Boolean,
        val mentionEveryone: Boolean,
        val mentions: List<Long>,
        val mentionRoles: List<Long>,
        val attachments: List<AttachmentData>,
        val embeds: List<EmbedData>,
        val reactions: List<ReactionData>? = null,
        val nonce: Long? = null,
        val pinned: Boolean,
        val webhookId: Long?,
        val type: MessageType,
        val activity: MessageActivity? = null,
        val application: MessageApplication? = null,
        val mentionedChannels: List<Long>? = null
) {

    fun plus(selfId: Snowflake, reaction: MessageReaction) : MessageData {
        val isMe = selfId.value == reaction.userId

        val reactions = if (reactions.isNullOrEmpty()) {
            listOf(ReactionData.from(1, isMe, reaction.emoji))
        } else {
            val reactions = reactions.orEmpty()
            val data = reactions.firstOrNull { data ->
                if (reaction.emoji.id == null) data.emojiName == reaction.emoji.name
                else data.emojiId?.toString() == reaction.emoji.id && data.emojiName == reaction.emoji.name
            }

            when (data) {
                null -> reactions + ReactionData.from(1, isMe, reaction.emoji)
                else -> (reactions - data) + data.copy(count = data.count + 1, me = isMe)
            }
        }

        return copy(reactions = reactions)
    }

    operator fun plus(partialMessage: PartialMessage): MessageData {

        val editedTimestamp = partialMessage.editedTimestamp ?: editedTimestamp
        val content = partialMessage.content ?: content
        val mentions = partialMessage.mentions.orEmpty().map { it.id.toLong() }
        val mentionEveryone = partialMessage.mentionEveryone ?: mentionEveryone
        val embeds = partialMessage.embeds?.map { EmbedData.from(it) } ?: embeds
        val mentionedChannels = partialMessage.mentionedChannels?.map { it.id.toLong() } //can't figure out if list hasn't been updated or just isn't there, so we'll assume the latter

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
                application,
                mentionedChannels
        )
    }

    companion object {
        val description get() = description(MessageData::id)

        fun from(entity: Message) = with(entity) {
            MessageData(
                    id.toLong(),
                    channelId.toLong(),
                    guildId?.toLong(),
                    author?.let { UserData.from(it) },
                    content,
                    timestamp,
                    editedTimestamp,
                    tts,
                    mentionEveryone,
                    mentions.map { it.id.toLong() },
                    mentionRoles.map { it.id.toLong() },
                    attachments.map { AttachmentData.from(it) },
                    embeds.map { EmbedData.from(it) },
                    reactions?.map { ReactionData.from(it) },
                    nonce?.toLong(),
                    pinned,
                    webhookId?.toLong(),
                    type,
                    activity,
                    application,
                    mentionedChannels?.map { it.id.toLong() }
            )
        }
    }
}
