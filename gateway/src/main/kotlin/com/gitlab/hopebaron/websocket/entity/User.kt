package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class User(
        val id: Snowflake,
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

@Serializable(with = Premium.PremiumSerializer::class)
enum class Premium(val code: Int) {
    NitroClassic(1),
    Nitro(2);

    @Serializer(forClass = Premium::class)
    companion object PremiumSerializer : KSerializer<Premium> {
        override val descriptor: SerialDescriptor
            get() = IntDescriptor.withName("premium_type")

        override fun deserialize(decoder: Decoder): Premium {
            val code = decoder.decodeInt()
            return values().first { it.code == code }
        }

        override fun serialize(encoder: Encoder, obj: Premium) {
            encoder.encodeInt(obj.code)
        }
    }
}