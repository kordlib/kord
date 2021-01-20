@file:Suppress("ArrayInDataClass")

package dev.kord.rest.json.response

import dev.kord.common.entity.DiscordTeam
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload gotten from [GET /oauth2/applications/@me](https://discord.com/developers/docs/topics/oauth2#get-current-application-information)
 */
@Serializable
data class ApplicationInfoResponse(
        val id: Snowflake,
        val name: String,
        val icon: String?,
        val description: String,
        @SerialName("rpc_origins")
        val rpcOrigins: Optional<List<String>> = Optional.Missing(),
        @SerialName("bot_public")
        val botPublic: Boolean,
        @SerialName("bot_require_code_grant")
        val botRequireCodeGrant: Boolean,
        val owner: DiscordUser,
        val summary: String,
        @SerialName("verify_key")
        val verifyKey: String,
        val team: DiscordTeam?,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("primary_sku_id")
        val primarySkuId: OptionalSnowflake = OptionalSnowflake.Missing,
        val slug: Optional<String> = Optional.Missing(),
        @SerialName("cover_image")
        val coverImage: Optional<String> = Optional.Missing()
)