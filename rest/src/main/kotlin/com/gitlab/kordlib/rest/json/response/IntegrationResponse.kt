package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.DiscordUser
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class IntegrationResponse(
        val id: String,
        val name: String,
        val type: String,
        val enabled: Boolean,
        val syncing: Boolean,
        @SerialName("role_id")
        val roleId: String,
        @SerialName("enable_emoticons")
        val enableEmoticons: Boolean? = null,
        @SerialName("expire_behavior")
        val expireBehavior: IntegrationExpireBehavior,
        @SerialName("expire_grace_period")
        val expireGracePeriod: Int,
        val user: DiscordUser,
        val account: DiscordIntegrationsAccount,
        @SerialName("synced_at")
        val syncedAt: String
)

@Suppress("unused")
@Serializable(with = IntegrationExpireBehavior.Serializer::class)
enum class IntegrationExpireBehavior(val code: Int) {
    RemoveRole(0), Kick(1), Unknown(-1);

    companion object Serializer : KSerializer<IntegrationExpireBehavior> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("expire_behavior", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): IntegrationExpireBehavior {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: IntegrationExpireBehavior) {
            encoder.encodeInt(value.code)
        }

    }

}

@Serializable
data class DiscordIntegrationsAccount(val id: String, val name: String)