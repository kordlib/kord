package com.gitlab.kordlib.core.event.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.Category
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event
import java.time.Instant

 class ChannelPinsUpdateEvent (
         val channelId: Snowflake,
         val lastPinTimestamp: Instant?,
         override val kord: Kord,
         override val shard: Int
) : Event {

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    suspend fun getChannel(): MessageChannel = kord.getChannel(channelId) as MessageChannel

}
