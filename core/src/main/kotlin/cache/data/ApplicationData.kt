package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordApplication
import dev.kord.common.entity.DiscordTeam
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.mapSnowflake
import kotlinx.serialization.Serializable

@Deprecated(
    "'ApplicationInfoData' was renamed to 'ApplicationData'.",
    ReplaceWith("ApplicationData", "dev.kord.core.cache.data.ApplicationData"),
    DeprecationLevel.ERROR,
)
public typealias ApplicationInfoData = ApplicationData

@Serializable
public data class ApplicationData(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val description: String,
    val rpcOrigins: Optional<List<String>> = Optional.Missing(),
    val botPublic: Boolean,
    val botRequireCodeGrant: Boolean,
    val termsOfServiceUrl: Optional<String> = Optional.Missing(),
    val privacyPolicyUrl: Optional<String> = Optional.Missing(),
    val ownerId: OptionalSnowflake = OptionalSnowflake.Missing,
    val summary: String,
    val verifyKey: String,
    val team: DiscordTeam?,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val primarySkuId: OptionalSnowflake = OptionalSnowflake.Missing,
    val slug: Optional<String> = Optional.Missing(),
    val coverImage: Optional<String> = Optional.Missing(),
    // TODO flags field
) {
    public companion object {

        public fun from(entity: DiscordApplication): ApplicationData = with(entity) {
            ApplicationData(
                id,
                name,
                icon,
                description,
                rpcOrigins,
                botPublic,
                botRequireCodeGrant,
                termsOfServiceUrl,
                privacyPolicyUrl,
                owner.mapSnowflake { it.id },
                summary,
                verifyKey,
                team,
                guildId,
                primarySkuId,
                slug,
                coverImage,
            )
        }
    }
}
