package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.DefaultSoundboardSoundBehavior
import dev.kord.core.behavior.GuildSoundboardSoundBehavior
import dev.kord.core.behavior.SoundboardSoundBehavior
import dev.kord.core.cache.data.SoundboardSoundData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * Representation of a soundboard sound.
 *
 * @property data the cache data
 * @property name the name of this sound
 * @property soundId the id of this sound
 * @property volume the volume of this sound, from `0.0` to `1.0`
 * @property emojiName the unicode character of this sound's standard emoji (if set)
 * @property available whether this sound can be used, may be false due to loss of Server Boosts
 */
public interface SoundboardSound : SoundboardSoundBehavior {
    public val data: SoundboardSoundData

    public val name: String get() = data.name
    public val soundId: Snowflake get() = data.id
    public val volume: Double get() = data.volume
    public val emojiName: String? get() = data.emojiName
    public val available: Boolean get() = data.available

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SoundboardSound
}

/**
 * A Soundboard Sound that everyone can use.
 *
 * @see SoundboardSound
 */
public class DefaultSoundboardSound(
    override val data: SoundboardSoundData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : DefaultSoundboardSoundBehavior, SoundboardSound {
    override val id: Snowflake get() = data.id
    override val emojiName: String
        get() = data.emojiName!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DefaultSoundboardSound =
        DefaultSoundboardSound(data, kord, strategy.supply(kord))
}

/**
 * A Soundboard Sound that can be used by everyone.
 *
 * @property emojiId the id of this sound's custom emoji
 * @property user the user who created this sound
 * @see SoundboardSound
 */
public class GuildSoundboardSound(
    override val data: SoundboardSoundData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : SoundboardSound, GuildSoundboardSoundBehavior {
    override val id: Snowflake get() = data.id
    override val emojiName: String get() = data.emojiName!!
    override val guildId: Snowflake get() = data.guildId.value!!
    public val emojiId: Snowflake? get() = data.emojiId
    public val user: User get() = User(data.user.value!!, kord, supplier)

    override suspend fun asGuildSoundboardSound(): GuildSoundboardSound = this
    override suspend fun asGuildSoundboardSoundOrNull(): GuildSoundboardSound = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildSoundboardSound =
        GuildSoundboardSound(data, kord, strategy.supply(kord))
}
