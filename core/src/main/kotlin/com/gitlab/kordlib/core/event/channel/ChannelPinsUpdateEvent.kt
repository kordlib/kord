package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.event.channel.data.ChannelPinsUpdateEventData
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import com.gitlab.kordlib.core.toInstant
import java.time.Instant

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
