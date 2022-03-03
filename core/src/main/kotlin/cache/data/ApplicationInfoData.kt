package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordTeam
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.rest.json.response.ApplicationInfoResponse

public data class ApplicationInfoData(
    val id: Snowflake,
    val name: String,
    val icon: String? = null,
    val description: String,
    val rpcOrigins: Optional<List<String>?> = Optional.Missing(),
    val botPublic: Boolean,
    val botRequireCodeGrant: Boolean,
    val ownerId: Snowflake,
    val verifyKey: String,
    val team: DiscordTeam? = null,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val primarySkuId: OptionalSnowflake = OptionalSnowflake.Missing,
    val slug: Optional<String> = Optional.Missing(),
    val coverImage: Optional<String> = Optional.Missing(),
) {
    public companion object {

        public fun from(entity: ApplicationInfoResponse): ApplicationInfoData = with(entity) {
            ApplicationInfoData(
                id,
                name,
                icon,
                description,
                rpcOrigins,
                botPublic,
                botRequireCodeGrant,
                owner.id,
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
