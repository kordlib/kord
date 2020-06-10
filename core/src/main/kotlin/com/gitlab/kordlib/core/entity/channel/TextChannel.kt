package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.TextChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.supplier.EntitySupplier

/**
 * An instance of a Discord Text Channel associated to a guild.
 */
class TextChannel(
        override val data: ChannelData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : GuildMessageChannel, TextChannelBehavior {

    /**
     * Whether the channel is nsfw.
     */
    val isNsfw: Boolean get() = data.nsfw!!

    /**
     * The amount of seconds a user has to wait before sending another message.
     */
    val userRateLimit: Int get() = data.rateLimitPerUser!!

    /**
     * returns a new [TextChannel] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannel =
            TextChannel(data, kord, strategy.supply(kord))

}
