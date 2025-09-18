package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordSoundboardSound
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.Optional.Missing.Companion.invoke
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.map
import kotlinx.serialization.Serializable

@Serializable
public class SoundboardSoundData(
    public val name: String,
    public val id: Snowflake,
    public val volume: Double,
    public val emojiId: Snowflake?,
    public val emojiName: String?,
    public val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    public val available: Boolean,
    public val user: Optional<UserData> = Optional.Missing()
) {
    public companion object {
        public val description: DataDescription<SoundboardSoundData, Snowflake> = description(SoundboardSoundData::id)

        public fun from(sound: DiscordSoundboardSound): SoundboardSoundData = with(sound) {
            SoundboardSoundData(
                name,
                soundId,
                volume,
                emojiId,
                emojiName,
                guildId,
                available,
                user.map(UserData::from)
            )
        }
    }
}
