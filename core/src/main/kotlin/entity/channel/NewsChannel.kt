package dev.kord.core.entity.channel

import dev.kord.common.entity.ArchiveDuration
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.NewsChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a Discord News Channel associated to a guild.
 */
class NewsChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, GuildMessageChannel, ThreadParentChannel,  NewsChannelBehavior {

    override suspend fun asChannel(): NewsChannel = this

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    /**
     * Returns a new [NewsChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsChannel =
        NewsChannel(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "NewsChannel(data=$data, kord=$kord, supplier=$supplier)"
    }

}