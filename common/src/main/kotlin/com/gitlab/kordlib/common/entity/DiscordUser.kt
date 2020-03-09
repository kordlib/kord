package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

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
        val flags: Int? = null,
        @SerialName("premium_type")
        val premiumType: Premium? = null,
        val verified: Boolean? = null,
        val email: String? = null
)

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
    NitroClassic(1),
    Nitro(2);

    @Serializer(forClass = Premium::class)
    companion object PremiumSerializer : KSerializer<Premium> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor("premium_type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Premium {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: Premium) {
            encoder.encodeInt(obj.code)
        }
    }
}