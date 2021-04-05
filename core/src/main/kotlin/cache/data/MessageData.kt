package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import kotlinx.serialization.Serializable

internal val MessageData.authorId get() = author.id

@Serializable
data class MessageData(
        val id: Snowflake,
        val channelId: Snowflake,
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val author: UserData,
        val content: String,
        val timestamp: String,
        val editedTimestamp: String? = null,
        val tts: Boolean,
        val mentionEveryone: Boolean,
        val mentions: List<Snowflake>,
        val mentionRoles: List<Snowflake>,
        val mentionedChannels: Optional<List<Snowflake>> = Optional.Missing(),
        val attachments: List<AttachmentData>,
        val embeds: List<EmbedData>,
        val reactions: Optional<List<ReactionData>> = Optional.Missing(),
        val nonce: Optional<String> = Optional.Missing(),
        val pinned: Boolean,
        val webhookId: OptionalSnowflake = OptionalSnowflake.Missing,
        val type: MessageType,
        val activity: Optional<MessageActivity> = Optional.Missing(),
        val application: Optional<MessageApplication> = Optional.Missing(),
        val messageReference: Optional<MessageReferenceData> = Optional.Missing(),
        val flags: Optional<MessageFlags> = Optional.Missing(),
        val stickers: Optional<List<MessageStickerData>> = Optional.Missing(),
        val referencedMessage: Optional<MessageData?> = Optional.Missing(),
) {

    fun plus(selfId: Snowflake, reaction: MessageReactionAddData): MessageData {
        val isMe = selfId == reaction.userId

        val reactions = if (reactions !is Optional.Value) {
            listOf(ReactionData.from(1, isMe, reaction.emoji))
        } else {
            val reactions = reactions.orEmpty()
            val data = reactions.firstOrNull { data ->
                if (reaction.emoji.id == null) data.emojiName == reaction.emoji.name
                else data.emojiId == reaction.emoji.id && data.emojiName == reaction.emoji.name
            }

            when (data) {
                null -> reactions + ReactionData.from(1, isMe, reaction.emoji)
                else -> (reactions - data) + data.copy(count = data.count + 1, me = isMe)
            }
        }

        return copy(reactions = Optional(reactions))
    }

    operator fun plus(partialMessage: DiscordPartialMessage): MessageData {

        val editedTimestamp = partialMessage.editedTimestamp.value ?: editedTimestamp
        val content = partialMessage.content.value ?: content
        val mentions = partialMessage.mentions.mapList { it.id }.value ?: mentions
        val mentionEveryone = partialMessage.mentionEveryone.orElse(mentionEveryone)
        val embeds = partialMessage.embeds.mapList { EmbedData.from(it) }.switchOnMissing(embeds).orEmpty()
        val mentionRoles = partialMessage.mentionRoles.mapList { it }.value ?: mentionRoles
        val mentionedChannels = partialMessage.mentionedChannels.mapList { it.id }.switchOnMissing(mentionedChannels.value.orEmpty()).coerceToMissing()
        val stickers = partialMessage.stickers.mapList { MessageStickerData.from(it) }.switchOnMissing(this.stickers)

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
                mentionedChannels,
                attachments,
                embeds,
                reactions,
                nonce,
                pinned,
                webhookId,
                type,
                activity,
                application,
                messageReference,
                flags,
                stickers = stickers,
        )
    }

    companion object {
        val description = description(MessageData::id)

        fun from(entity: DiscordMessage): MessageData = with(entity) {
            MessageData(
                    id,
                    channelId,
                    guildId,
                    UserData.from(author),
                    content,
                    timestamp,
                    editedTimestamp,
                    tts,
                    mentionEveryone,
                    mentions.map { it.id },
                    mentionRoles,
                    mentionedChannels.mapList { it.id },
                    attachments.map { AttachmentData.from(it) },
                    embeds.map { EmbedData.from(it) },
                    reactions.mapList { ReactionData.from(it) },
                    nonce,
                    pinned,
                    webhookId,
                    type,
                    activity,
                    application,
                    messageReference.map { MessageReferenceData.from(it) },
                    flags,
                    stickers.mapList { MessageStickerData.from(it) },
                    referencedMessage.mapNotNull { from(it) }
            )
        }
    }
}

fun DiscordMessage.toData() = MessageData.from(this)