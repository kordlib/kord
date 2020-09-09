package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.IntDescriptor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Serializable
data class DiscordUser(
        val id: String,
        val username: String,
        val discriminator: String,
        val avatar: String? = null,
        val bot: Boolean? = null,
        @SerialName("mfa_enable")
        val mfaEnable: Boolean? = null,
        val locale: String? = null,
        val flags: UserFlags? = null,
        @SerialName("premium_type")
        val premiumType: Premium? = null,
        val verified: Boolean? = null,
        val email: String? = null
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


    @Serializer(forClass = UserFlags::class)
    companion object UserFlagsSerializer : DeserializationStrategy<UserFlags> {

        inline operator fun invoke(builder: UserFlagsBuilder.() -> Unit): UserFlags {
            return UserFlagsBuilder().apply(builder).flags()
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("userFlag", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): UserFlags {
            val flags = decoder.decodeInt()
            return UserFlags(flags)
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


@Serializable
data class DiscordOptionallyMemberUser(
        val id: String,
        val username: String,
        val discriminator: String,
        val avatar: String? = null,
        val bot: Boolean? = null,
        @SerialName("mfa_enable")
        val mfaEnable: Boolean? = null,
        val locale: String? = null,
        val flags: Int? = null,
        @SerialName("premium_type")
        val premiumType: Premium? = null,
        val member: DiscordPartialGuildMember? = null
)

@Serializable(with = Premium.PremiumSerializer::class)
enum class Premium(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    None(0),
    NitroClassic(1),
    Nitro(2);

    @Serializer(forClass = Premium::class)
    companion object PremiumSerializer : KSerializer<Premium> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("premium_type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Premium {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: Premium) {
            encoder.encodeInt(value.code)
        }
    }
}