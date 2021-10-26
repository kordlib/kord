package dev.kord.core.entity.channel

import dev.kord.common.entity.optional.getOrThrow
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.VoiceChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.Region
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.firstOrNull
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * An instance of a Discord Voice Channel associated to a guild.
 */
public class VoiceChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : CategorizableChannel, TopGuildMessageChannel, VoiceChannelBehavior {


    /**
     * The bitrate (in bits) of this channel.
     */
    public val bitrate: Int get() = data.bitrate.getOrThrow()

    /**
     * The user limit of the voice channel.
     */
    public val userLimit: Int get() = data.userLimit.getOrThrow()

    /**
     * The region name of the voice channel
     */
    public val rtcRegion: String? get() = data.rtcRegion.value

    /**
     * Requests to get the [voice region][Region] of this channel.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Region] wasn't present.
     * @throws [NoSuchElementException] if the [rtcRegion] is not in the available.
     */
    public suspend fun getRegion(): Region = guild.regions.first { it.id == rtcRegion }

    /**
     * Requests to get the [voice region][Region] of this channel.
     *
     * returns null if the region was not found
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getRegionOrNull(): Region? = guild.regions.firstOrNull { it.id == rtcRegion }

    /**
     * returns a new [VoiceChannel] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceChannel =
        VoiceChannel(data, kord, strategy.supply(kord))

    override suspend fun asChannel(): VoiceChannel = this

    override suspend fun asChannelOrNull(): VoiceChannel = this

    override suspend fun fetchChannelOrNull(): VoiceChannel? = supplier.getChannelOfOrNull(id)

    override suspend fun fetchChannel(): VoiceChannel = supplier.getChannelOf(id)

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "VoiceChannel(data=$data, kord=$kord, supplier=$supplier)"
    }
}
