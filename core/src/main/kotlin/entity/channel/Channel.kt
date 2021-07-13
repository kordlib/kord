package dev.kord.core.entity.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.ChannelType.*
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.thread.NewsThreadChannel
import dev.kord.core.entity.channel.thread.TextThreadChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

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
            GuildStageVoice -> StageChannel(data, kord)
            GuildVoice -> VoiceChannel(data, kord)
            GuildCategory -> Category(data, kord)
            GuildNews -> NewsChannel(data, kord)
            GuildStore -> StoreChannel(data, kord)
            PublicNewsThread -> NewsThreadChannel(data, kord)
            PrivateThread -> TextThreadChannel(data, kord)
            PublicGuildThread -> TextThreadChannel(data, kord)

            else -> object : Channel {
                override val data: ChannelData = data
                override val kord: Kord = kord
                override val supplier: EntitySupplier = strategy.supply(kord)
            }
        }
    }
}
