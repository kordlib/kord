package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * The behavior of a Discord channel associated to a [guild].
 */
public interface GuildChannelBehavior : ChannelBehavior, Strategizable {

    /**
     * The id of the guild this channel is associated to.
     */
    public val guildId: Snowflake

    /**
     * The guild behavior this channel is associated to.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get this behavior as a [TopGuildChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a guild channel.
     */
    override suspend fun asChannel(): GuildChannel = super.asChannel() as GuildChannel

    /**
     * Requests to get this behavior as a [TopGuildChannel],
     * returns null if the channel isn't present or if the channel isn't a guild channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): GuildChannel? = super.asChannelOrNull() as? GuildChannel

    /**
     * Retrieve the [GuildChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): GuildChannel = super.fetchChannel() as GuildChannel


    /**
     * Retrieve the [GuildChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [GuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): GuildChannel? = super.fetchChannelOrNull() as? GuildChannel

    /**
     * Requests to get this channel's [Guild].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get this channel's [Guild],
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)


    override fun compareTo(other: Entity): Int {
        if (other !is GuildChannelBehavior) return super.compareTo(other)
        val discordOrder = compareBy<GuildChannelBehavior> { it.guildId }
            .thenBy { (it as? TopGuildChannel)?.guildId }
            .thenBy { it.id }

        return discordOrder.compare(this, other)
    }

    /**
     * Returns a new [TopGuildChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(
        strategy: EntitySupplyStrategy<*>
    ): GuildChannelBehavior = GuildChannelBehavior(guildId, id, kord, strategy)


}

public fun GuildChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): GuildChannelBehavior = object : GuildChannelBehavior {
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
        return "GuildChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}

