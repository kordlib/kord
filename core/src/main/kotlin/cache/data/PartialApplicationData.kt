package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordPartialApplication
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
public data class PartialApplicationData(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val description: String,
    val termsOfServiceUrl: Optional<String> = Optional.Missing(),
    val privacyPolicyUrl: Optional<String> = Optional.Missing(),
    val summary: String,
    val verifyKey: String,
    val coverImage: Optional<String> = Optional.Missing(),
) {
    public companion object {

        public fun from(entity: DiscordPartialApplication): PartialApplicationData = with(entity) {
            PartialApplicationData(
                id,
                name,
                icon,
                description,
                termsOfServiceUrl,
                privacyPolicyUrl,
                summary,
                verifyKey,
                coverImage,
            )
        }
    }
}
