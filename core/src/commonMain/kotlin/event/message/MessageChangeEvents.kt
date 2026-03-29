package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public interface MessagesChangeEvent : Event, Strategizable {
    public val channelId: Snowflake
    public val guildId: Snowflake?
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }
}

public sealed interface MessageChangeEvent : MessagesChangeEvent {
    public val messageId: Snowflake
    public val message: MessageBehavior?
}