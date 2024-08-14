package dev.kord.core.behavior.channel

import dev.kord.common.exception.RequestException
import dev.kord.core.behavior.channel.threads.ThreadOnlyChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.MediaChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.MediaChannelModifyBuilder
import dev.kord.rest.service.patchMediaChannel
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface MediaChannelBehavior : ThreadOnlyChannelBehavior {

    /**
     * Requests to get this behavior as a [MediaChannel].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the channel wasn't present.
     * @throws ClassCastException if the channel isn't a [MediaChannel].
     */
    override suspend fun asChannel(): MediaChannel = super.asChannel() as MediaChannel

    /**
     * Requests to get this behavior as a [MediaChannel],
     * returns null if the channel isn't present or if the channel isn't a [MediaChannel].
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): MediaChannel? = super.asChannelOrNull() as? MediaChannel

    /**
     * Retrieve the [MediaChannel] associated with this behavior from the provided [EntitySupplier].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the user wasn't present.
     */
    override suspend fun fetchChannel(): MediaChannel = super.fetchChannel() as MediaChannel

    /**
     * Retrieve the [MediaChannel] associated with this behavior from the provided [EntitySupplier]
     * returns null if the [MediaChannel] isn't present.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): MediaChannel? = super.fetchChannelOrNull() as? MediaChannel

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MediaChannelBehavior
}

public suspend inline fun MediaChannelBehavior.edit(builder: MediaChannelModifyBuilder.() -> Unit): MediaChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.patchMediaChannel(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as MediaChannel
}
