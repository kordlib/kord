@file:GenerateKordEnum(
    name = "UserPremium", valueType = INT,
    kDoc = "Premium types denote the level of premium a user has.",
    docUrl = "https://discord.com/developers/docs/resources/user#user-object-premium-types",
    entries = [
        Entry("None", intValue = 0),
        Entry("NitroClassic", intValue = 1),
        Entry("Nitro", intValue = 2),
        Entry("NitroBasic", intValue = 3)
    ],
)

@file:GenerateKordEnum(
    name = "UserFlag", valueType = INT,
    isFlags = true,
    kDoc = "https://discord.com/developers/docs/resources/user#user-object-user-flags",
    entries = [
        Entry("DiscordEmployee", intValue = 1 shl 0, kDoc = "Discord Employee"),
        Entry("DiscordPartner", intValue = 1 shl 1, kDoc = "Partnered Server Owner"),
        Entry("HypeSquad", intValue = 1 shl 2, kDoc = "HypeSquad Events Member"),
        Entry("BugHunterLevel1", intValue = 1 shl 3, kDoc = "Bug Hunter Level 1"),
        Entry("HouseBravery", intValue = 1 shl 6, kDoc = "House Bravery Member"),
        Entry("HouseBrilliance", intValue = 1 shl 7, kDoc = "House Brilliance Member"),
        Entry("HouseBalance", intValue = 1 shl 8, kDoc = "House Balance Member"),
        Entry("EarlySupporter", intValue = 1 shl 9, kDoc = "Early Nitro Supporter"),
        Entry("TeamUser", intValue = 1 shl 10, kDoc = "User is a team"),
        Entry("BugHunterLevel2", intValue = 1 shl 14, kDoc = "Bug Hunter Level 2"),
        Entry("VerifiedBot", intValue = 1 shl 16, kDoc = "Verified Bot"),
        Entry("VerifiedBotDeveloper", intValue = 1 shl 17, kDoc = "Early Verified Bot Developer"),
        Entry("DiscordCertifiedModerator", intValue = 1 shl 18, kDoc = "Moderator Programs Alumni"),
        Entry(
            "BotHttpInteractions",
            intValue = 1 shl 19,
            kDoc = "Bot uses only HTTP interactions and is shown in the online member list"
        ),
        Entry("ActiveDeveloper", intValue = 1 shl 22, kDoc = "User is an Active Developer"),
        Entry("System", intValue = 1 shl 12)
    ]
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A representation of the [Discord User structure](https://discord.com/developers/docs/resources/user).
 *
 * @param id The user's id.
 * @param username the user's username, not unique across the platform.
 * @param discriminator the 4-digit discord-tag.
 * @param avatar the user's avatar hash.
 * @param bot Whether the user belongs to an OAuth2 application.
 * @param system whether the user is an Official Discord System user (part of the urgent message system).
 * @param mfaEnabled Whether the user has two factor enabled on their account.
 * @param locale The user's chosen language option.
 * @param verified Whether the email on this account has been verified. Requires the `email` OAuth2 scope.
 * @param email The user's email. Requires the `email` OAuth2 scope.
 * @param flags The flags on a user's account. Unlike [publicFlags], these **are not** visible to other users.
 * @param premiumType The type of Nitro subscription on a user's account.
 * @param publicFlags The public flags on a user's account. Unlike [flags], these **are** visible ot other users.
 */
@Serializable
public data class DiscordUser(
    val id: Snowflake,
    val username: String,
    val discriminator: String,
    val avatar: String?,
    val bot: OptionalBoolean = OptionalBoolean.Missing,
    val system: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("mfa_enabled")
    val mfaEnabled: OptionalBoolean = OptionalBoolean.Missing,
    val locale: Optional<String> = Optional.Missing(),
    val verified: OptionalBoolean = OptionalBoolean.Missing,
    val email: Optional<String?> = Optional.Missing(),
    val flags: Optional<UserFlags> = Optional.Missing(),
    @SerialName("premium_type")
    val premiumType: Optional<UserPremium> = Optional.Missing(),
    @SerialName("public_flags")
    val publicFlags: Optional<UserFlags> = Optional.Missing(),
    val banner: String? = null,
    @SerialName("accent_color")
    val accentColor: Int? = null
)

/**
 * A representation of the [Discord User structure](https://discord.com/developers/docs/resources/user).
 * This instance also contains a [member].
 *
 * @param id The user's id.
 * @param username the user's username, not unique across the platform.
 * @param discriminator the 4-digit discord-tag.
 * @param avatar the user's avatar hash.
 * @param bot Whether the user belongs to an OAuth2 application.
 * @param system whether the user is an Official Discord System user (part of the urgent message system).
 * @param mfaEnabled Whether the user has two factor enabled on their account.
 * @param locale The user's chosen language option.
 * @param verified Whether the email on this account has been verified. Requires the `email` OAuth2 scope.
 * @param email The user's email. Requires the `email` OAuth2 scope.
 * @param flags The flags on a user's account. Unlike [publicFlags], these **are not** visible to other users.
 * @param premiumType The type of Nitro subscription on a user's account.
 * @param publicFlags The public flags on a user's account. Unlike [flags], these **are** visible ot other users.
 */
@Serializable
public data class DiscordOptionallyMemberUser(
    val id: Snowflake,
    val username: String,
    val discriminator: String,
    val avatar: String?,
    val bot: OptionalBoolean = OptionalBoolean.Missing,
    val system: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("mfa_enabled")
    val mfaEnabled: OptionalBoolean = OptionalBoolean.Missing,
    val locale: Optional<String> = Optional.Missing(),
    val verified: OptionalBoolean = OptionalBoolean.Missing,
    val email: Optional<String?> = Optional.Missing(),
    val flags: Optional<UserFlags> = Optional.Missing(),
    @SerialName("premium_type")
    val premiumType: Optional<UserPremium> = Optional.Missing(),
    @SerialName("public_flags")
    val publicFlags: Optional<UserFlags> = Optional.Missing(),
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("member", "guild_member")
    val member: Optional<DiscordGuildMember> = Optional.Missing(),
)
