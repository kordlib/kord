package dev.kord.core.behavior.channel

import dev.kord.common.exception.RequestException
import dev.kord.core.behavior.channel.threads.ThreadOnlyChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ForumChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.ForumChannelModifyBuilder
import dev.kord.rest.service.patchForumChannel
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ForumChannelBehavior : ThreadOnlyChannelBehavior {

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
     * Retrieve the [ForumChannel] associated with this behavior from the provided [EntitySupplier].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the user wasn't present.
     */
    override suspend fun fetchChannel(): ForumChannel = super.fetchChannel() as ForumChannel

    /**
     * Retrieve the [ForumChannel] associated with this behavior from the provided [EntitySupplier]
     * returns null if the [ForumChannel] isn't present.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): ForumChannel? = super.fetchChannelOrNull() as? ForumChannel

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ForumChannelBehavior
}

public suspend inline fun ForumChannelBehavior.edit(builder: ForumChannelModifyBuilder.() -> Unit): ForumChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.patchForumChannel(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ForumChannel
}
