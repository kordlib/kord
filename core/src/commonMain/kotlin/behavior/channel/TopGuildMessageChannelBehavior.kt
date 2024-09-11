package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The behavior of a non-thread Discord message channel associated to a [guild].
 *
 * 'Top' channels are those that do not require a parent channel to be created, and can be found at the top of the UI's hierarchy.
 *
 */
public interface TopGuildMessageChannelBehavior : CategorizableChannelBehavior, GuildMessageChannelBehavior {

    /**
     * Requests to get this behavior as a [TopGuildMessageChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a guild message channel.
     */
    override suspend fun asChannel(): TopGuildMessageChannel =
        super<CategorizableChannelBehavior>.asChannel() as TopGuildMessageChannel

    /**
     * Requests to get this behavior as a [TopGuildMessageChannel],
     * returns null if the channel isn't present or if the channel isn't a guild channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): TopGuildMessageChannel? =
        super<CategorizableChannelBehavior>.asChannelOrNull() as? TopGuildMessageChannel

    /**
     * Retrieve the [TopGuildMessageChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): TopGuildMessageChannel =
        super<CategorizableChannelBehavior>.fetchChannel() as TopGuildMessageChannel


    /**
     * Retrieve the [TopGuildMessageChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [TopGuildMessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): TopGuildMessageChannel? =
        super<CategorizableChannelBehavior>.fetchChannelOrNull() as? TopGuildMessageChannel

    /**
     * Returns a new [TopGuildMessageChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TopGuildMessageChannelBehavior =
        TopGuildMessageChannelBehavior(guildId, id, kord, strategy)
}

internal fun TopGuildMessageChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
) = object : TopGuildMessageChannelBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "TopGuildMessageChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}
