package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representation of a [Soundboard Sound](https://discord.com/developers/docs/resources/soundboard#soundboard-sound-object-soundboard-sound-structure).
 *
 * @property name the name of this sound
 * @property soundId the id of this sound
 * @property volume the volume of this sound, from `0.0` to `1.0`
 * @property emojiId the id of this sound's custom emoji (if set)
 * @property emojiName the unicode character of this sound's standard emoji (if set)
 * @property guildId the id of the guild this sound is in, or [OptionalSnowflake.Missing] for default sounds
 * @property available whether this sound can be used, may be false due to loss of Server Boosts
 * @property user the user who created this sound, or [OptionalSnowflake.Missing] for default sounds
 */
@Serializable
public data class DiscordSoundboardSound(
    val name: String,
    @SerialName("sound_id")
    val soundId: Snowflake,
    val volume: Double,
    @SerialName("emoji_id")
    val emojiId: Snowflake?,
    @SerialName("emoji_name")
    val emojiName: String?,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val available: Boolean,
    val user: Optional<DiscordUser> = Optional.Missing()
)
