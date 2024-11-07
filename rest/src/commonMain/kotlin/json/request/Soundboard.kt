package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalDouble
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to play a soundboard sound.
 *
 * @property soundId the id of the soundboard sound to play
 * @property sourceGuildId the id of the guild the soundboard sound is from, required to play sounds from different servers
 */
@Serializable
public data class SendSoundboardSoundRequest(
    @SerialName("sound_id")
    val soundId: Snowflake,
    @SerialName("source_guild_id")
    val sourceGuildId: OptionalSnowflake = OptionalSnowflake.Missing,
)

/**
 * Representation of a [Soundboard Sound](https://discord.com/developers/docs/resources/soundboard#soundboard-sound-object-soundboard-sound-structure).
 *
 * @property name name of the soundboard sound (2-32 characters)
 * @property sound the mp3 or ogg sound data, base64 encoded
 * @property volume the volume of the soundboard sound, from `0.0` to `1.0`, defaults to `1.0`
 * @property emojiId the id of this sound's custom emoji (if set)
 * @property emojiName the unicode character of this sound's standard emoji (if set)
 */
@Serializable
public data class CreateSoundboardSoundRequest(
    val name: String,
    val sound: String,
    val volume: OptionalDouble? = OptionalDouble.Missing,
    @SerialName("emoji_id")
    val emojiId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("emoji_name")
    val emojiName: Optional<String?> = Optional.Missing(),
)

/**
 * Representation of a [Soundboard Sound](https://discord.com/developers/docs/resources/soundboard#soundboard-sound-object-soundboard-sound-structure).
 *
 * @property name name of the soundboard sound (2-32 characters)
 * @property volume the volume of the soundboard sound, from `0.0` to `1.0`, defaults to `1.0`
 * @property emojiId the id of this sound's custom emoji (if set)
 * @property emojiName the unicode character of this sound's standard emoji (if set)
 */
@Serializable
public data class UpdateSoundboardSoundRequest(
    val name: Optional<String> = Optional.Missing(),
    val volume: OptionalDouble? = OptionalDouble.Missing,
    @SerialName("emoji_id")
    val emojiId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("emoji_name")
    val emojiName: Optional<String?> = Optional.Missing(),
)