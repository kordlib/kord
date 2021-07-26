package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.threads.ChannelThreadBehavior
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

interface ThreadUserBehavior : UserBehavior {

    val threadId: Snowflake

    val thread: ChannelThreadBehavior get() = ChannelThreadBehavior(threadId, kord)

    suspend fun getThread(): ThreadChannel {
        return supplier.getChannelOf(threadId)
    }


    suspend fun getThreadOrNull(): ThreadChannel? {
        return supplier.getChannelOfOrNull(threadId)
    }
}