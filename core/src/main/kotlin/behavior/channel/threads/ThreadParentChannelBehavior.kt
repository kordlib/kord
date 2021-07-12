package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.entity.channel.thread.ThreadChannel
import kotlinx.coroutines.flow.Flow

interface ThreadParentChannelBehavior : GuildChannelBehavior {

    val publicActiveThreads: Flow<ThreadChannel> get() = supplier.getActiveThreads(id)

    fun getPublicArchivedThreads(messageId: Snowflake, limit: Int = Int.MAX_VALUE) {

    }



}