package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.NewsChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a Discord News Channel associated to a guild.
 */
data class NewsChannel(
        override val data: ChannelData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, GuildMessageChannel, NewsChannelBehavior {

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
}