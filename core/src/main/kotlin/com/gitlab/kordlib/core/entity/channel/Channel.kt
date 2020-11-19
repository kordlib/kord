package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.ChannelType.*
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

/**
 * An instance of a [Discord Channel](https://discord.com/developers/docs/resources/channel)
 */
interface Channel : ChannelBehavior {
    val data: ChannelData

    override val id: Snowflake
        get() = data.id

    /**
     * The type of this channel.
     */
    val type: ChannelType get() = data.type

    /**
     * Returns a new [Channel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Channel =
            from(data, kord, strategy)

    companion object {

        /**
         * Creates a [Channel] of the type defined in the [ChannelData.type].
         * If the type is not any known type, then an anonymous channel will be created.
         */
        fun from(
                data: ChannelData,
                kord: Kord,
                strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): Channel = when (data.type) {
            GuildText -> TextChannel(data, kord)
            DM, GroupDM -> DmChannel(data, kord)
            GuildVoice -> VoiceChannel(data, kord)
            GuildCategory -> Category(data, kord)
            GuildNews -> NewsChannel(data, kord)
            GuildStore -> StoreChannel(data, kord)
            else -> object : Channel {
                override val data: ChannelData = data
                override val kord: Kord = kord
                override val supplier: EntitySupplier = strategy.supply(kord)
            }
        }
    }
}
