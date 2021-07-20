package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.entity.channel.thread.ThreadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Behavior of channels that can create public threads.
 */
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

/**
 * Behavior of channels that can create private threads.
 * This derives from [ThreadParentChannelBehavior]
 * since Discord allows all public operations on private thread parents.
 */
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
