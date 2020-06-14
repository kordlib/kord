package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import java.time.Instant

class ChannelPinsUpdateEvent(
        val channelId: Snowflake,
        val lastPinTimestamp: Instant?,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ChannelPinsUpdateEvent =
            ChannelPinsUpdateEvent(channelId, lastPinTimestamp, kord, strategy.supply(kord))
}
