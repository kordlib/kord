package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
data class DiscordUser(
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
data class DiscordOptionallyMemberUser(
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
        val member: Optional<DiscordGuildMember> = Optional.Missing(),
)


enum class UserFlag(val code: Int) {
    None(0),
    DiscordEmployee(1 shl 0),
    DiscordPartner(1 shl 1),
    HypeSquad(1 shl 2),
    BugHunterLevel1(1 shl 3),
    HouseBravery(1 shl 6),
    HouseBrilliance(1 shl 7),
    HouseBalance(1 shl 8),
    EarlySupporter(1 shl 9),
    TeamUser(1 shl 10),
    System(1 shl 12),
    BugHunterLevel2(1 shl 14),
    VerifiedBot(1 shl 16),
    VerifiedBotDeveloper(1 shl 17)
}

@Serializable(with = UserFlags.UserFlagsSerializer::class)
data class UserFlags constructor(val code: Int) {

    val flags = UserFlag.values().filter { code and it.code != 0 }

    operator fun contains(flag: UserFlag) = flag in flags

    operator fun plus(flags: UserFlags): UserFlags = when {
        code and flags.code == flags.code -> this
        else -> UserFlags(this.code or flags.code)
    }

    operator fun minus(flag: UserFlag): UserFlags = when {
        code and flag.code == flag.code -> UserFlags(code xor flag.code)
        else -> this
    }

    @OptIn(ExperimentalContracts::class)
    inline fun copy(block: UserFlagsBuilder.() -> Unit): UserFlags {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val builder = UserFlagsBuilder(code)
        builder.apply(block)
        return builder.flags()
    }


    companion object UserFlagsSerializer : KSerializer<UserFlags> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("userFlag", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): UserFlags {
            val flags = decoder.decodeInt()
            return UserFlags(flags)
        }

        override fun serialize(encoder: Encoder, value: UserFlags) {
            encoder.encodeInt(value.code)
        }

    }

    class UserFlagsBuilder(internal var code: Int = 0) {
        operator fun UserFlag.unaryPlus() {
            this@UserFlagsBuilder.code = this@UserFlagsBuilder.code or code
        }

        operator fun UserFlag.unaryMinus() {
            if (this@UserFlagsBuilder.code and code == code) {
                this@UserFlagsBuilder.code = this@UserFlagsBuilder.code xor code
            }
        }

        fun flags() = UserFlags(code)
    }

}


@OptIn(ExperimentalContracts::class)
inline fun UserFlags(builder: UserFlags.UserFlagsBuilder.() -> Unit): UserFlags {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return UserFlags.UserFlagsBuilder().apply(builder).flags()
}

/**
 * An instance of [Discord Premium Types](https://discord.com/developers/docs/resources/user#user-object-premium-types).
 *
 * Premium types denote the level of premium a user has.
 */
@Serializable(with = UserPremium.Serialization::class)
sealed class UserPremium(val value: Int) {
    class Unknown(value: Int) : UserPremium(value)
    object None : UserPremium(0)
    object NitroClassic : UserPremium(1)
    object Nitro : UserPremium(2)

    companion object;

    internal object Serialization : KSerializer<UserPremium> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("premium_type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): UserPremium = when (val value = decoder.decodeInt()) {
            0 -> None
            1 -> NitroClassic
            2 -> Nitro
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: UserPremium) {
            encoder.encodeInt(value.value)
        }
    }
}