@file:GenerateKordEnum(
    name = "ApplicationFlag",
    valueType = GenerateKordEnum.ValueType.INT,
    isFlags = true,
    entries = [
        GenerateKordEnum.Entry(name = "GatewayPresence", intValue = 1 shl 12, kDoc = """
         Intent required for bots in **100 or more servers** to receive
         [`PresenceUpdate`·events](https://discord.com/developers/docs/topics/gateway#presence-update)."""),
        GenerateKordEnum.Entry(name = "GatewayPresenceLimited", intValue = 1 shl 13, kDoc = """
         Intent required for bots in under 100 servers to receive
         [`PresenceUpdate`·events](https://discord.com/developers/docs/topics/gateway#presence-update), found in Bot
         Settings."""),
        GenerateKordEnum.Entry(name = "GatewayGuildMembers", intValue = 1 shl 14, kDoc = """
         Intent required for bots in **100 or more servers** to receive member-related events like `GuildMemberAdd`.
         
         See list of member-related events under
         [`GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents)."""),

        GenerateKordEnum.Entry(name = "GatewayGuildMembersLimited", intValue = 1 shl 15, kDoc = """
         Intent required for bots in under 100 servers to receive member-related events like `GuildMemberAdd`, found in
         Bot Settings.
         
         See list of member-related events under
         [`GUILD_MEMBERS`](https://discord.com/developers/docs/topics/gateway#list-of-intents)."""),
        GenerateKordEnum.Entry(name = "VerificationPendingGuildLimit", intValue = 1 shl 16, kDoc = "Indicates unusual growth of an app that prevents verification."),
        GenerateKordEnum.Entry(name = "Embedded", intValue = 1 shl 17, kDoc = "Indicates if an app is embedded within the Discord client (currently unavailable publicly)."),
        GenerateKordEnum.Entry(name = "GatewayMessageContent", intValue = 1 shl 18, kDoc = """
         Intent required for bots in **100 or more servers*to receive
         [message content](https://support-dev.discord.com/hc/en-us/articles/4404772028055)."""),

        GenerateKordEnum.Entry(name = "GatewayMessageContentLimited", intValue = 1 shl 19, kDoc = """
         Intent required for bots in under 100 servers to receive
         [message·content](https://support-dev.discord.com/hc/en-us/articles/4404772028055), found in Bot Settings."""),
        GenerateKordEnum.Entry(name = "ApplicationCommandBadge", intValue = 1 shl 23, kDoc = "Indicates if an app has registered global application commands.")
    ]
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.GenerateKordEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public sealed interface BaseDiscordApplication {
    public val id: Snowflake
    public val name: String
    public val icon: String?
    public val description: String
    public val rpcOrigins: Optional<List<String>>
    public val termsOfServiceUrl: Optional<String>
    public val privacyPolicyUrl: Optional<String>
    public val owner: Optional<DiscordUser>
    public val verifyKey: String
    public val guildId: OptionalSnowflake
    public val primarySkuId: OptionalSnowflake
    public val slug: Optional<String>
    public val coverImage: Optional<String>
    public val flags: Optional<ApplicationFlags>
    public val tags: Optional<List<String>>
    public val installParams: Optional<InstallParams>
    public val customInstallUrl: Optional<String>
}

/**
 A representation of the
 [Application Structure](https://discord.com/developers/docs/resources/application#application-object-application-structure).
 */
@Serializable
public data class DiscordApplication(
    override val id: Snowflake,
    override val name: String,
    override val icon: String?,
    override val description: String,
    @SerialName("rpc_origins")
    override val rpcOrigins: Optional<List<String>> = Optional.Missing(),
    @SerialName("bot_public")
    val botPublic: Boolean,
    @SerialName("bot_require_code_grant")
    val botRequireCodeGrant: Boolean,
    @SerialName("terms_of_service_url")
    override val termsOfServiceUrl: Optional<String> = Optional.Missing(),
    @SerialName("privacy_policy_url")
    override val privacyPolicyUrl: Optional<String> = Optional.Missing(),
    override val owner: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("verify_key")
    override val verifyKey: String,
    val team: DiscordTeam?,
    @SerialName("guild_id")
    override val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("primary_sku_id")
    override val primarySkuId: OptionalSnowflake = OptionalSnowflake.Missing,
    override val slug: Optional<String> = Optional.Missing(),
    @SerialName("cover_image")
    override val coverImage: Optional<String> = Optional.Missing(),
    override val flags: Optional<ApplicationFlags> = Optional.Missing(),
    override val tags: Optional<List<String>> = Optional.Missing(),
    @SerialName("install_params")
    override val installParams: Optional<InstallParams> = Optional.Missing(),
    @SerialName("custom_install_url")
    override val customInstallUrl: Optional<String> = Optional.Missing(),
) : BaseDiscordApplication

/**
 A representation of the partial
 [Application Structure](https://discord.com/developers/docs/resources/application#application-object-application-structure)
 sent in [invite create events](https://discord.com/developers/docs/topics/gateway#invite-create).
 */
@Serializable
public data class DiscordPartialApplication(
    override val id: Snowflake,
    override val name: String,
    override val icon: String?,
    override val description: String,
    @SerialName("rpc_origins")
    override val rpcOrigins: Optional<List<String>> = Optional.Missing(),
    @SerialName("terms_of_service_url")
    override val termsOfServiceUrl: Optional<String> = Optional.Missing(),
    @SerialName("privacy_policy_url")
    override val privacyPolicyUrl: Optional<String> = Optional.Missing(),
    override val owner: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("verify_key")
    override val verifyKey: String,
    @SerialName("guild_id")
    override val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("primary_sku_id")
    override val primarySkuId: OptionalSnowflake = OptionalSnowflake.Missing,
    override val slug: Optional<String> = Optional.Missing(),
    @SerialName("cover_image")
    override val coverImage: Optional<String> = Optional.Missing(),
    override val flags: Optional<ApplicationFlags> = Optional.Missing(),
    override val tags: Optional<List<String>> = Optional.Missing(),
    @SerialName("install_params")
    override val installParams: Optional<InstallParams> = Optional.Missing(),
    @SerialName("custom_install_url")
    override val customInstallUrl: Optional<String> = Optional.Missing(),
) : BaseDiscordApplication

@Serializable
public data class InstallParams(
    /*The scopes to add the application to the server with. */
    val scopes: List<String>,
    /*The permissions to request for the bot role. */
    val permissions: Permissions,
)
