package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
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
        val member: Optional<DiscordGuildMember>,
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

        inline operator fun invoke(builder: UserFlagsBuilder.() -> Unit): UserFlags {
            return UserFlagsBuilder().apply(builder).flags()
        }

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