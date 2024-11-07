package dev.kord.core.behavior

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permission.Speak
import dev.kord.common.entity.Permission.UseExternalSounds
import dev.kord.common.entity.Permission.UseSoundboard
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.VoiceChannelBehavior
import dev.kord.core.cache.data.SoundboardSoundData
import dev.kord.core.entity.GuildSoundboardSound
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.VoiceState
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.guild.SoundboardSoundModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.modifyGuildSoundboardSound
import kotlin.contracts.contract

/**
 * Behavior of a discord soundboard sound.
 *
 * @see DefaultSoundboardSoundBehavior
 * @see GuildSoundboardSoundBehavior
 */
public interface SoundboardSoundBehavior : KordEntity, Strategizable {

    /**
     * Plays this sound in [channel].
     *
     * This requires the [Permission.Speak] and [Permission.UseSoundboard] permissions, and also the
     * [Permission.UseExternalSounds] permission if the sound is from a different server.
     * Additionally, requires the user to be connected to the voice channel,
     * having a [voice state] without [VoiceState.isDeafened], [VoiceState.isSelfDeafened], [VoiceState.isMuted],
     * or [VoiceState.isSuppressed] enabled.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun send(channel: VoiceChannelBehavior)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SoundboardSoundBehavior
}

/**
 * Behavior of a default sound board sound.
 */
public interface DefaultSoundboardSoundBehavior : SoundboardSoundBehavior {
    override suspend fun send(channel: VoiceChannelBehavior): Unit =
        kord.rest.channel.sendSoundboardSound(id, channel.id)
}

/**
 * Behavior of guild soundboard sounds.
 */
public interface GuildSoundboardSoundBehavior : SoundboardSoundBehavior {
    /**
     * The id of the guild the sound is on.
     */
    public val guildId: Snowflake

    /**
     * The behavior of the guild this emoji is part of.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    override suspend fun send(channel: VoiceChannelBehavior): Unit =
        kord.rest.channel.sendSoundboardSound(id, channel.id, guildId)

    /**
     * Deletes this sound.
     *
     * This requires the [Permission.ManageGuildExpressions] permission.
     *
     * @param reason the audit log reason for this change
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun delete(reason: String? = null): Unit =
        kord.rest.guild.deleteGuildSoundboardSound(guildId, id, reason)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildSoundboardSoundBehavior
}

/**
 * Modifies this sound.
 */
public suspend inline fun GuildSoundboardSoundBehavior.edit(builder: SoundboardSoundModifyBuilder.() -> Unit): GuildSoundboardSound {
    contract {
        callsInPlace(builder, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }

    val response = kord.rest.guild.modifyGuildSoundboardSound(guildId, id, builder)
    val data = SoundboardSoundData.from(response)

    return GuildSoundboardSound(data, kord, supplier)
}
