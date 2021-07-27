package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.rest.json.request.StartThreadRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Behavior of channels that can contain public threads.
 */
interface ThreadParentChannelBehavior : GuildMessageChannelBehavior {

    val activeThreads: Flow<ThreadChannel> get() = supplier.getActiveThreads(id)

    fun getPublicArchivedThreads(
        before: Instant = Clock.System.now(),
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getPublicArchivedThreads(id, before, limit)
    }

    /**
     * Starts a public thread with [name] and archived after [archiveDuration]
     */
    suspend fun startPublicThread(name: String, archiveDuration: ArchiveDuration = ArchiveDuration.Day): ThreadChannel


    /**
     * Starts a public thread with [name] and archived after [archiveDuration]
     * using given [messageId] as a starter message.
     */
    suspend fun startPublicThreadWithMessage(
        messageId: Snowflake,
        name: String,
        archiveDuration: ArchiveDuration = ArchiveDuration.Day
    ): ThreadChannel {

        val response = kord.rest.channel.startPublicThread(id, messageId, StartThreadRequest(name, archiveDuration))
        val data = ChannelData.from(response)

        return Channel.from(data, kord) as ThreadChannel
    }


}

/**
 * Behavior of channels that can contain private threads.
 * This derives from [ThreadParentChannelBehavior]
 * since Discord allows all public operations on private thread parents.
 */
interface PrivateThreadParentChannelBehavior : ThreadParentChannelBehavior {

    fun getPrivateArchivedThreads(
        before: Instant = Clock.System.now(),
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getPrivateArchivedThreads(id, before, limit)
    }

    fun getJoinedPrivateArchivedThreads(
        before: Instant = Clock.System.now(),
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getJoinedPrivateArchivedThreads(id, before, limit)
    }

    /**
     * Starts a private thread with [name] and archived after [archiveDuration]
     */
    suspend fun startPrivateThread(name: String, archiveDuration: ArchiveDuration = ArchiveDuration.Day): ThreadChannel
}


internal suspend fun ThreadParentChannelBehavior.startThread(
    name: String,
    archiveDuration: ArchiveDuration,
    type: ChannelType
): ThreadChannel {

    val response =
        kord.rest.channel.startPrivateThread(id, StartThreadRequest(name, archiveDuration, Optional.Value(type)))
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ThreadChannel
}