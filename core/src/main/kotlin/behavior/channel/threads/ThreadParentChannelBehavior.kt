package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.entity.channel.thread.ThreadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


interface ThreadParentChannelBehavior : GuildMessageChannelBehavior {

    val publicActiveThreads: Flow<ThreadChannel> get() = supplier.getActiveThreads(id)

    fun getPublicArchivedThreads(
        channelId: Snowflake,
        before: Instant,
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getPublicArchivedThreads(channelId, before, limit)
    }

}

interface PrivateThreadParentChannelBehavior : ThreadParentChannelBehavior {

    fun getPrivateArchivedThreads(
        channelId: Snowflake,
        before: Instant,
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getPrivateArchivedThreads(channelId, before, limit)
    }

    fun getJoinedPrivateArchivedThreads(
        channelId: Snowflake,
        before: Instant,
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getJoinedPrivateArchivedThreads(channelId, before, limit)
    }

}
