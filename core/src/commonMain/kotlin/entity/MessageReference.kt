package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.MessageReferenceData

/**
 * Represents a [message reference object](https://discord.com/developers/docs/resources/channel#message-reference-object).
 *
 * @param data The [MessageReferenceData] for the reference
 */
public class MessageReference(public val data: MessageReferenceData, override val kord: Kord) : KordObject {

    /** The [GuildBehavior] of the guild the referenced message is in. */
    public val guild: GuildBehavior? get() = data.guildId.value?.let { GuildBehavior(it, kord) }

    /** The [MessageChannelBehavior] of the channel the referenced message is in. */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(data.channelId.value!!, kord)

    /** The [MessageBehavior] of the referenced message. */
    public val message: MessageBehavior? get() = data.id.value?.let { MessageBehavior(channel.id, it, kord) }


}
