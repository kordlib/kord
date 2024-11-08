package dev.kord.core.behavior.channel

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.SoundboardSoundBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.SoundboardSound
import dev.kord.core.entity.VoiceState
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.VoiceChannelModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.patchVoiceChannel
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord Voice Channel associated to a guild.
 */
public interface VoiceChannelBehavior : TopGuildMessageChannelBehavior, BaseVoiceChannelBehavior {

    /**
     * Plays [sound] in this channel.
     *
     * This requires the [Permission.Speak] and [Permission.UseSoundboard] permissions, and also the
     * [Permission.UseExternalSounds] permission if the sound is from a different server.
     * Additionally, requires the user to be connected to the voice channel,
     * having a [voice state] without [VoiceState.isDeafened], [VoiceState.isSelfDeafened], [VoiceState.isMuted],
     * or [VoiceState.isSuppressed] enabled.
     *
     * @see SoundboardSoundBehavior.play
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun playSound(sound: SoundboardSoundBehavior): Unit = sound.send(this)

    /**
     * Requests to get the this behavior as a [VoiceChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [VoiceChannel].
     */
    override suspend fun asChannel(): VoiceChannel = super<BaseVoiceChannelBehavior>.asChannel() as VoiceChannel

    /**
     * Requests to get this behavior as a [VoiceChannel],
     * returns null if the channel isn't present or if the channel isn't a [VoiceChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): VoiceChannel? = super<BaseVoiceChannelBehavior>.asChannelOrNull() as? VoiceChannel

    /**
     * Retrieve the [VoiceChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): VoiceChannel = super<BaseVoiceChannelBehavior>.fetchChannel() as VoiceChannel


    /**
     * Retrieve the [VoiceChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [VoiceChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): VoiceChannel? = super<BaseVoiceChannelBehavior>.fetchChannelOrNull() as? VoiceChannel

    /**
     * Returns a new [VoiceChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceChannelBehavior =
        VoiceChannelBehavior(guildId, id, kord, strategy)
}

public fun VoiceChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): VoiceChannelBehavior = object : VoiceChannelBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "VoiceChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to edit this channel.
 *
 * @return The edited [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun VoiceChannelBehavior.edit(builder: VoiceChannelModifyBuilder.() -> Unit): VoiceChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.channel.patchVoiceChannel(id, builder)

    val data = ChannelData.from(response)
    return Channel.from(data, kord) as VoiceChannel
}
