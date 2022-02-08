package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A representation of the
 * [Application Structure](https://discord.com/developers/docs/resources/application#application-object-application-structure).
 */
@Serializable
public data class DiscordApplication(
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
    @SerialName("terms_of_service_url")
    val termsOfServiceUrl: Optional<String> = Optional.Missing(),
    @SerialName("privacy_policy_url")
    val privacyPolicyUrl: Optional<String> = Optional.Missing(),
    val owner: Optional<DiscordUser> = Optional.Missing(),
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
    val coverImage: Optional<String> = Optional.Missing(),
    // TODO flags field and type
)

/**
 * A representation of the partial
 * [Application Structure](https://discord.com/developers/docs/resources/application#application-object-application-structure)
 * sent in [invite create events](https://discord.com/developers/docs/topics/gateway#invite-create).
 */
@Serializable
public data class DiscordPartialApplication(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val description: String,
    @SerialName("terms_of_service_url")
    val termsOfServiceUrl: Optional<String> = Optional.Missing(),
    @SerialName("privacy_policy_url")
    val privacyPolicyUrl: Optional<String> = Optional.Missing(),
    val summary: String,
    @SerialName("verify_key")
    val verifyKey: String,
    @SerialName("cover_image")
    val coverImage: Optional<String> = Optional.Missing(),
)
