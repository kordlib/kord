package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.VoiceChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.supplier.EntitySupplier

/**
 * An instance of a Discord Voice Channel associated to a guild.
 */
class VoiceChannel(
        override val data: ChannelData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, VoiceChannelBehavior {


    /**
     * The bitrate (in bits) of this channel.
     */
    val bitrate: Int get() = data.bitrate!!

    /**
     * The user limit of the voice channel.
     */
    val userLimit: Int get() = data.userLimit!!

    /**
     * returns a new [VoiceChannel] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceChannel = VoiceChannel(data, kord, strategy.supply(kord))

}
