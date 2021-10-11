package dev.kord.core.entity.channel

import dev.kord.common.entity.optional.getOrThrow
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.StageChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a [Discord Stage Channel](https://support.discord.com/hc/en-us/articles/1500005513722)
 * associated to a community guild.
 */
public class StageChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, StageChannelBehavior {

    /**
     * The bitrate (in bits) of this channel.
     */
    public val bitrate: Int get() = data.bitrate.getOrThrow()

    /**
     * The user limit of the voice channel.
     */
    public val userLimit: Int get() = data.userLimit.getOrThrow()

    /**
     * returns a new [StageChannel] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StageChannel =
        StageChannel(data, kord, strategy.supply(kord))

    override suspend fun asChannel(): StageChannel = this

    override suspend fun asChannelOrNull(): StageChannel = this

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "StageChannel(data=$data, kord=$kord, supplier=$supplier)"
    }
}
