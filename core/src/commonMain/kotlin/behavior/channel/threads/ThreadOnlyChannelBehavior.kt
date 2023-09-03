package dev.kord.core.behavior.channel.threads

import dev.kord.common.exception.RequestException
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ThreadOnlyChannel
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.thread.StartForumThreadBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ThreadOnlyChannelBehavior : ThreadParentChannelBehavior {

    override val activeThreads: Flow<TextChannelThread> get() = super.activeThreads.filterIsInstance()

    override fun getPublicArchivedThreads(before: Instant?, limit: Int?): Flow<TextChannelThread> =
        super.getPublicArchivedThreads(before, limit).filterIsInstance()

    public suspend fun startPublicThread(name: String, builder: StartForumThreadBuilder.() -> Unit): TextChannelThread =
        unsafeStartThreadInThreadOnlyChannel(name, builder)

    /**
     * Requests to get this behavior as a [ThreadOnlyChannel].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the channel wasn't present.
     * @throws ClassCastException if the channel isn't a [ThreadOnlyChannel].
     */
    override suspend fun asChannel(): ThreadOnlyChannel = super.asChannel() as ThreadOnlyChannel

    /**
     * Requests to get this behavior as a [ThreadOnlyChannel],
     * returns null if the channel isn't present or if the channel isn't a [ThreadOnlyChannel].
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): ThreadOnlyChannel? = super.asChannelOrNull() as? ThreadOnlyChannel

    /**
     * Retrieve the [ThreadOnlyChannel] associated with this behavior from the provided [EntitySupplier].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the user wasn't present.
     */
    override suspend fun fetchChannel(): ThreadOnlyChannel = super.fetchChannel() as ThreadOnlyChannel

    /**
     * Retrieve the [ThreadOnlyChannel] associated with this behavior from the provided [EntitySupplier]
     * returns null if the [ThreadOnlyChannel] isn't present.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): ThreadOnlyChannel? = super.fetchChannelOrNull() as? ThreadOnlyChannel

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadOnlyChannelBehavior
}

internal suspend fun ThreadParentChannelBehavior.unsafeStartThreadInThreadOnlyChannel(
    name: String,
    builder: StartForumThreadBuilder.() -> Unit,
): TextChannelThread {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.startForumThread(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannelThread
}
