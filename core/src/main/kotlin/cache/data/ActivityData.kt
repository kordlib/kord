package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable

@Serializable
data class ActivityData(
    val name: String,
    val type: ActivityType,
    val url: Optional<String?> = Optional.Missing(),
    val createdAt: Long,
    val timestamps: Optional<DiscordActivityTimeStamps> = Optional.Missing(),
    val applicationId: OptionalSnowflake = OptionalSnowflake.Missing,
    val details: Optional<String?> = Optional.Missing(),
    val state: Optional<String?> = Optional.Missing(),
    val emoji: Optional<DiscordActivityEmoji?> = Optional.Missing(),
    val party: Optional<DiscordActivityParty> = Optional.Missing(),
    val assets: Optional<DiscordActivityAssets> = Optional.Missing(),
    val secrets: Optional<DiscordActivitySecrets> = Optional.Missing(),
    val instance: OptionalBoolean = OptionalBoolean.Missing,
    val flags: Optional<ActivityFlags> = Optional.Missing(),
    val buttons: Optional<List<String>> = Optional.Missing()
) {
    companion object {
        fun from(entity: DiscordActivity) = with(entity) {
            ActivityData(
                name,
                type,
                url,
                createdAt,
                timestamps,
                applicationId,
                details,
                state,
                emoji,
                party,
                assets,
                secrets,
                instance,
                flags
            )
        }
    }


}