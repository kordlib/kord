package dev.kord.core.behavior.channel

import dev.kord.common.exception.RequestException
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ForumChannel
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.ForumChannelModifyBuilder
import dev.kord.rest.builder.channel.thread.StartForumThreadBuilder
import dev.kord.rest.service.patchForumChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ForumChannelBehavior : ThreadParentChannelBehavior {

    override val activeThreads: Flow<TextChannelThread>
        get() = super.activeThreads.filterIsInstance()

    override fun getPublicArchivedThreads(before: Instant?, limit: Int?): Flow<TextChannelThread> {
        return super.getPublicArchivedThreads(before, limit).filterIsInstance()
    }

    public suspend fun startPublicThread(
        name: String,
        builder: StartForumThreadBuilder.() -> Unit = {},
    ): TextChannelThread {
        return unsafeStartForumThread(name, builder)
    }

    /**
     * Requests to get this behavior as a [ForumChannel].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the channel wasn't present.
     * @throws ClassCastException if the channel isn't a [ForumChannel].
     */
    override suspend fun asChannel(): ForumChannel = super.asChannel() as ForumChannel

    /**
     * Requests to get this behavior as a [ForumChannel],
     * returns null if the channel isn't present or if the channel isn't a [ForumChannel].
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): ForumChannel? = super.asChannelOrNull() as? ForumChannel

    /**
     * Retrieve the [ForumChannel] associated with this behaviour from the provided [EntitySupplier].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the user wasn't present.
     */
    override suspend fun fetchChannel(): ForumChannel = super.fetchChannel() as ForumChannel

    /**
     * Retrieve the [ForumChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [ForumChannel] isn't present.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): ForumChannel? = super.fetchChannelOrNull() as? ForumChannel

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ForumChannelBehavior
}

internal suspend fun ThreadParentChannelBehavior.unsafeStartForumThread(
    name: String,
    builder: StartForumThreadBuilder.() -> Unit,
): TextChannelThread {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.startForumThread(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannelThread
}

public suspend inline fun ForumChannelBehavior.edit(builder: ForumChannelModifyBuilder.() -> Unit): ForumChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.patchForumChannel(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ForumChannel
}
