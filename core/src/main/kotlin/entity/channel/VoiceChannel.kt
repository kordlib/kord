package dev.kord.core.entity.channel

import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.optional.getOrThrow
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.behavior.channel.VoiceChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.exception.GatewayNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.voice.VoiceConnection
import dev.kord.voice.VoiceConnectionBuilder
import java.util.*

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
    val bitrate: Int get() = data.bitrate.getOrThrow()

    /**
     * The user limit of the voice channel.
     */
    val userLimit: Int get() = data.userLimit.getOrThrow()

    /**
     * returns a new [VoiceChannel] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceChannel =
        VoiceChannel(data, kord, strategy.supply(kord))

    override suspend fun asChannel(): VoiceChannel = this

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "VoiceChannel(data=$data, kord=$kord, supplier=$supplier)"
    }

    /**
     * Connect to this [VoiceChannel] and create a [VoiceConnection] for this voice session.
     *
     * @param builder a builder for the [VoiceConnection].
     * @throws GatewayNotFoundException when there is no associated [dev.kord.gateway.Gateway] for the [dev.kord.core.entity.Guild] this channel is in.
     * @throws dev.kord.voice.exception.VoiceConnectionInitializationException when there was a problem retrieving voice information from Discord.
     * @return a [VoiceConnection] representing the connection to this [VoiceConnection].
     */
    @KordVoice
    suspend fun connect(builder: VoiceConnectionBuilder.() -> Unit): VoiceConnection {
        val voiceConnection = VoiceConnection(
            getGuild().gateway ?: GatewayNotFoundException.voiceConnectionGatewayNotFound(guildId),
            kord.selfId,
            id,
            guildId,
            builder
        )

        voiceConnection.connect()

        return voiceConnection
    }
}