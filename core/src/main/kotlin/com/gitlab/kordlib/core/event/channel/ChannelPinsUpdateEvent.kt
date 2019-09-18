package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event
import java.time.Instant

 class ChannelPinsUpdateEvent internal constructor(
        val channelId: Snowflake,
        val lastPinTimestamp: Instant?,
        override val kord: Kord
) : Event {

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    suspend fun getChannel(): MessageChannel = kord.getChannel(channelId) as MessageChannel

}
