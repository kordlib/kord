package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.request.RestRequestException
import java.util.*

/**
 * The behavior of a [Discord Channel](https://discord.com/developers/docs/resources/channel)
 */
interface ChannelBehavior : Entity, Strategizable {

    /**
     * This channel [formatted as a mention](https://discord.com/developers/docs/reference#message-formatting)
     * as used by the Discord API.
     */
    val mention get() = "<#${id.value}>"

    /**
     * Requests to get this behavior as a [Channel] .
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     */
    suspend fun asChannel(): Channel = supplier.getChannel(id)

    /**
     * Requests to get this behavior as a [Channel],
     * returns null if the channel isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun asChannelOrNull(): Channel? = supplier.getChannelOrNull(id)

    /**
     * Requests to delete a channel (or close it if this is a dm channel).
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete() {
        kord.rest.channel.deleteChannel(id.value)
    }

    /**
     * Returns a new [ChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>) : ChannelBehavior = ChannelBehavior(id, kord, strategy)

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy) = object : ChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)


            override fun hashCode(): Int = Objects.hash(id)

            override fun equals(other: Any?): Boolean = when(other) {
                is ChannelBehavior -> other.id == id
                else -> false
            }
        }
    }
}