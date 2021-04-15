package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

class ReactionRemoveAllEvent(
    val channelId: Snowflake,
    val messageId: Snowflake,
    val guildId: Snowflake?,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    val message: MessageBehavior get() = MessageBehavior(channelId, messageId, kord)

    suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)
    suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)
    suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveAllEvent =
        ReactionRemoveAllEvent(channelId, messageId, guildId, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "ReactionRemoveAllEvent(channelId=$channelId, messageId=$messageId, guildId=$guildId, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}