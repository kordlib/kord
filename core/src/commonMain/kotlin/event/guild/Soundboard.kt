package dev.kord.core.event.guild

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.GuildSoundboardSound
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.VoiceChannelEffect

/**
 * Event fired when a new [GuildSoundboardSound] is created.
 *
 * @property sound the newly created sound
 */
public class GuildSoundboardSoundCreateEvent(
    public val sound: GuildSoundboardSound,
    override val shard: Int, @KordPreview
    override val customContext: Any?, override val kord: Kord
) : Event

/**
 * Event fired when a [GuildSoundboardSound] is updated.
 *
 * @property sound the updated sound
 * @property old the old value of the sound (if cached)
 */
public class GuildSoundboardSoundUpdateEvent(
    public val old: GuildSoundboardSound?,
    public val sound: GuildSoundboardSound,
    override val shard: Int, @KordPreview
    override val customContext: Any?, override val kord: Kord
) : Event

/**
 * Event fired when a [GuildSoundboardSounds][GuildSoundboardSound] are updated.
 *
 * @property sounds the updated sounds
 */
public class GuildSoundboardSoundsUpdateEvent(
    public val sounds: List<UpdatedGuildSoundboardSound>,
    override val shard: Int, @KordPreview
    override val customContext: Any?, override val kord: Kord
) : Event {
    /**
     * Representation of a sound update.
     *
     * @property old the old value of the sound if cache
     * @property sound the updated values of the sound
     */
    public data class UpdatedGuildSoundboardSound(val old: GuildSoundboardSound?, val sound: GuildSoundboardSound)
}

/**
 * Event fired when a new [GuildSoundboardSound] is deleted.
 *
 * @property soundId the id of the deleted sound
 * @property guildId the id of the guild the sound was on
 * @property old the old value of the sound (if cached)
 */
public class GuildSoundboardSoundDeletEvent(
    public val old: GuildSoundboardSound?,
    public val soundId: Snowflake,
    public val guildId: Snowflake,
    override val shard: Int, @KordPreview
    override val customContext: Any?, override val kord: Kord
) : Event

/**
 * Event fired when a [VoiceChannelEffect] is sent.
 *
 * @property effect the effect
 */
public class VoiceChannelEffectSentEvent(
    public val effect: VoiceChannelEffect,
    override val shard: Int, @KordPreview
    override val customContext: Any?, override val kord: Kord
) : Event

public class SoundboardSounds(
    public val sounds: List<GuildSoundboardSound>,
    public val guildId: Snowflake,
    override val shard: Int,
    @KordPreview
    override val customContext: Any?,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable = SoundboardSounds(
        sounds,
        guildId,
        shard,
        customContext,
        kord,
        strategy.supply(kord)
    )
}
