package dev.kord.core.entity

import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.MessageType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.MessageReferenceData
import dev.kord.core.cache.data.MessageSnapshotData
import kotlinx.datetime.Instant

public class MessageReference(public val data: MessageReferenceData, override val kord: Kord) : KordObject {

    public val guild: GuildBehavior? get() = data.guildId.value?.let { GuildBehavior(it, kord) }

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(data.channelId.value!!, kord)

    public val message: MessageBehavior? get() = data.id.value?.let { MessageBehavior(channel.id, it, kord) }

}

public class MessageSnapshot(public val data: MessageSnapshotData, override val kord: Kord) : KordObject {
    private val message: MessageData get() = data.message
    public val type: MessageType get() = message.type
    public val content: String get() = message.content
    public val embeds: List<EmbedData> get() = message.embeds
    public val attachments: List<Attachment> get() = message.attachments.map { Attachment(it, kord) }
    public val timestamp: Instant get() = message.timestamp
    public val editedTimestamp: Instant? get() = message.editedTimestamp
    public val flags: MessageFlags? get() = message.flags.value
    public val mentions: List<Snowflake> get() = message.mentions
    public val mentionRoles: List<Snowflake> get() = message.mentionRoles
}
