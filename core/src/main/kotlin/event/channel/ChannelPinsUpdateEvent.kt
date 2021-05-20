package dev.kord.core.event.channel

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.channel.data.ChannelPinsUpdateEventData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

class ChannelPinsUpdateEvent(
    val data: ChannelPinsUpdateEventData,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    val channelId: Snowflake get() = data.channelId

    val guildId: Snowflake? get() = data.guildId.value

    val lastPinTimestamp: Instant? get() = data.lastPinTimestamp.value?.toInstant()

    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ChannelPinsUpdateEvent =
        ChannelPinsUpdateEvent(data, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "ChannelPinsUpdateEvent(channelId=$channelId, lastPinTimestamp=$lastPinTimestamp, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
