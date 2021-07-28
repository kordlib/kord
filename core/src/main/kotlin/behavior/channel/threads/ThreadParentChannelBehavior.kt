package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ThreadParentChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.json.request.StartThreadRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Behavior of channels that can contain public threads.
 */
interface ThreadParentChannelBehavior : GuildMessageChannelBehavior {
    /**
     * Returns all active public and private threads in the channel.
     * Threads are ordered by their id, in descending order.
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.

     */
    val activeThreads: Flow<ThreadChannel> get() = supplier.getActiveThreads(id)

    /**
     * Returns archived threads in the channel that are public.
     * Threads are ordered by [ThreadChannel.archiveTimeStamp] in descending order.
     * Requires the [Read Message History Permission][dev.kord.common.entity.Permission.ReadMessageHistory]
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getPublicArchivedThreads(
        before: Instant = Clock.System.now(),
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getPublicArchivedThreads(id, before, limit)
    }


    override suspend fun asChannel(): ThreadParentChannel {
        return super.asChannel() as ThreadParentChannel
    }

    override suspend fun asChannelOrNull(): ThreadParentChannel? {
        return super.asChannelOrNull() as? ThreadParentChannel
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadParentChannelBehavior

}

/**
 * Behavior of channels that can contain private threads.
 * This derives from [ThreadParentChannelBehavior]
 * since Discord allows all public operations on private thread parents.
 */
interface PrivateThreadParentChannelBehavior : ThreadParentChannelBehavior {

    /**
     * Returns archived threads in the channel that are private.
     * Threads are ordered by [archive timestamp][ThreadChannel.archiveTimeStamp] in descending order.
     * Requires the [Read Message History Permission][dev.kord.common.entity.Permission.ReadMessageHistory] and
     * [Manage Threads Permission][dev.kord.common.entity.Permission.ManageThreads]
     *
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getPrivateArchivedThreads(
        before: Instant = Clock.System.now(),
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getPrivateArchivedThreads(id, before, limit)
    }

    /**
     * Returns archived threads in the channel that are private, and the user has joined.
     * Threads are ordered by their [id][ThreadChannel.id] in descending order.
     * Requires the [Read Message History Permission][dev.kord.common.entity.Permission.ReadMessageHistory].
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    fun getJoinedPrivateArchivedThreads(
        before: Snowflake = Snowflake(Long.MAX_VALUE),
        limit: Int = Int.MAX_VALUE
    ): Flow<ThreadChannel> {
        return supplier.getJoinedPrivateArchivedThreads(id, before, limit)
    }
}

/**
 * starts a thread in the current thread parent based on [type] using given [name] and archived after [archiveDuration].
 * [type] should match the parent types.
 * @throws [RequestException] if something went wrong during the request.
 */
internal suspend fun ThreadParentChannelBehavior.unsafeStartThread(
    name: String,
    archiveDuration: ArchiveDuration = ArchiveDuration.Day,
    type: ChannelType,
    reason: String? = null
): ThreadChannel {

    val response =
        kord.rest.channel.startThread(id, StartThreadRequest(name, archiveDuration, Optional.Value(type)), reason)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ThreadChannel
}

internal suspend fun ThreadParentChannelBehavior.unsafeStartPublicThreadWithMessage(
    messageId: Snowflake,
    name: String,
    archiveDuration: ArchiveDuration = ArchiveDuration.Day,
    reason: String? = null
): ThreadChannel {

    val response = kord.rest.channel.startThreadWithMessage(id, messageId, StartThreadRequest(name, archiveDuration), reason)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ThreadChannel
}

internal fun ThreadParentChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ThreadParentChannelBehavior {
    return object : ThreadParentChannelBehavior {
        override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadParentChannelBehavior {
            return ThreadParentChannelBehavior(guildId, id, kord, strategy.supply(kord))
        }

        override val guildId: Snowflake
            get() = guildId
        override val kord: Kord
            get() = kord
        override val id: Snowflake
            get() = id
        override val supplier: EntitySupplier
            get() = supplier

    }
}


internal fun PrivateThreadParentChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): PrivateThreadParentChannelBehavior {
    return object : PrivateThreadParentChannelBehavior {
        override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadParentChannelBehavior {
            return PrivateThreadParentChannelBehavior(guildId, id, kord, strategy.supply(kord))
        }

        override val guildId: Snowflake
            get() = guildId
        override val kord: Kord
            get() = kord
        override val id: Snowflake
            get() = id
        override val supplier: EntitySupplier
            get() = supplier

    }
}
