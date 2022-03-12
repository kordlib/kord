@file:Suppress("DEPRECATION")

package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.StoreChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.StoreChannelModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.patchStoreChannel
import java.util.*
import kotlin.DeprecationLevel.WARNING
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord Store Channel associated to a guild.
 */
@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
    """,
    level = WARNING,
)
public interface StoreChannelBehavior : CategorizableChannelBehavior {

    /**
     * Requests to get the this behavior as a [StoreChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [StoreChannel].
     */
    override suspend fun asChannel(): StoreChannel = super.asChannel() as StoreChannel

    /**
     * Requests to get this behavior as a [StoreChannel],
     * returns null if the channel isn't present or if the channel isn't a [StoreChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): StoreChannel? = super.asChannelOrNull() as? StoreChannel

    /**
     * Retrieve the [StoreChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): StoreChannel = super.fetchChannel() as StoreChannel


    /**
     * Retrieve the [StoreChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [StoreChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): StoreChannel? = super.fetchChannelOrNull() as? StoreChannel

    /**
     * returns a new [StoreChannelBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.cacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StoreChannelBehavior =
        StoreChannelBehavior(guildId, id, kord, strategy)

}

@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
    """,
    level = WARNING,
)
public fun StoreChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): StoreChannelBehavior = object : StoreChannelBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "StoreChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to edit this channel.
 *
 * @return The edited [StoreChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Deprecated(
    """
    Discord no longer offers the ability to purchase a license to sell PC games on Discord and store channels were
    removed on March 10, 2022.
    
    See https://support-dev.discord.com/hc/en-us/articles/4414590563479 for more information.
    """,
    level = WARNING,
)
public suspend inline fun StoreChannelBehavior.edit(builder: StoreChannelModifyBuilder.() -> Unit): StoreChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchStoreChannel(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as StoreChannel
}
