package dev.kord.core.entity.channel

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.behavior.channel.StoreChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a Discord Store Channel associated to a guild.
 */
data class StoreChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, TopGuildChannel, StoreChannelBehavior {


    override suspend fun asChannel(): StoreChannel = this

    override suspend fun asChannelOrNull(): StoreChannel = this

    /**
     * Returns a new [StoreChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): StoreChannel =
        StoreChannel(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "StoreChannel(data=$data, kord=$kord, supplier=$supplier)"
    }

}