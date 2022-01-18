package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.TopGuildMessageChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ThreadParentChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.thread.StartThreadBuilder
import dev.kord.rest.json.request.StartThreadRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.Instant
import java.util.*

/**
 * Behavior of channels that can contain public threads.
 */
public interface ThreadParentChannelBehavior : TopGuildMessageChannelBehavior {
    /**
     * Returns all active public and private threads in the channel.
     * Threads are ordered by their id, in descending order.
     *
     *  The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.

     */
    public val activeThreads: Flow<ThreadChannel> get() = supplier.getActiveThreads(guildId).filter { it.parentId == id }

    /**
     * Returns archived threads in the channel that are public.
     * Threads are ordered by [ThreadChannel.archiveTimestamp] in descending order.
     * Requires the [Read Message History Permission][dev.kord.common.entity.Permission.ReadMessageHistory]
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getPublicArchivedThreads(
        before: Instant? = null,
        limit: Int? = null,
    ): Flow<ThreadChannel> {
        return supplier.getPublicArchivedThreads(id, before, limit)
    }


    override suspend fun asChannel(): ThreadParentChannel {
        return super.asChannel() as ThreadParentChannel
    }

    override suspend fun asChannelOrNull(): ThreadParentChannel? {
        return super.asChannelOrNull() as? ThreadParentChannel
    }

    /**
     * Retrieve the [ThreadParentChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): ThreadParentChannel = super.fetchChannel() as ThreadParentChannel


    /**
     * Retrieve the [ThreadParentChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [ThreadParentChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): ThreadParentChannel? = super.fetchChannelOrNull() as? ThreadParentChannel

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadParentChannelBehavior

}

/**
 * Behavior of channels that can contain private threads.
 * This derives from [ThreadParentChannelBehavior]
 * since Discord allows all public operations on private thread parents.
 */
public interface PrivateThreadParentChannelBehavior : ThreadParentChannelBehavior {

    /**
     * Returns archived threads in the channel that are private.
     * Threads are ordered by [archive timestamp][ThreadChannel.archiveTimestamp] in descending order.
     * Requires the [Read Message History Permission][dev.kord.common.entity.Permission.ReadMessageHistory] and
     * [Manage Threads Permission][dev.kord.common.entity.Permission.ManageThreads]
     *
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public fun getPrivateArchivedThreads(
        before: Instant? = null,
        limit: Int? = null,
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
    public fun getJoinedPrivateArchivedThreads(
        before: Snowflake? = null,
        limit: Int? = null,
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
    builder: StartThreadBuilder.() -> Unit
): ThreadChannel {
    val response =
        kord.rest.channel.startThread(id, name, archiveDuration, type, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ThreadChannel
}

internal suspend fun ThreadParentChannelBehavior.unsafeStartPublicThreadWithMessage(
    messageId: Snowflake,
    name: String,
    archiveDuration: ArchiveDuration = ArchiveDuration.Day,
    reason: String? = null
): ThreadChannel {

    val response =
        kord.rest.channel.startThreadWithMessage(id, messageId, StartThreadRequest(name, archiveDuration), reason)
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

        override fun hashCode(): Int = Objects.hash(id, guildId)

        override fun equals(other: Any?): Boolean = when (other) {
            is GuildChannelBehavior -> other.id == id && other.guildId == guildId
            is ChannelBehavior -> other.id == id
            else -> false
        }

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

        override fun hashCode(): Int = Objects.hash(id, guildId)

        override fun equals(other: Any?): Boolean = when (other) {
            is GuildChannelBehavior -> other.id == id && other.guildId == guildId
            is ChannelBehavior -> other.id == id
            else -> false
        }

    }
}
