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

@Serializable
data class DiscordIntegration(
        val id: Snowflake,
        val name: String,
        val type: String,
        val enabled: Boolean,
        val syncing: Boolean,
        @SerialName("role_id")
        val roleId: Snowflake,
        @SerialName("enable_emoticons")
        val enableEmoticons: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("expire_behavior")
        val expireBehavior: IntegrationExpireBehavior,
        @SerialName("expire_grace_period")
        val expireGracePeriod: Int,
        val user: DiscordUser,
        val account: DiscordIntegrationsAccount,
        @SerialName("synced_at")
        val syncedAt: String,
        val subscriberCount: Int,
        val revoked: Boolean,
        val application: IntegrationApplication
)

@Serializable
data class IntegrationApplication(
        val id: Snowflake,
        val name: String,
        val icon: String?,
        val description: String,
        val summary: String,
        val bot: Optional<DiscordUser>
)

@Serializable(with = IntegrationExpireBehavior.Serializer::class)
sealed class IntegrationExpireBehavior(val value: Int) {
    class Unknown(value: Int) : IntegrationExpireBehavior(value)
    object RemoveRole : IntegrationExpireBehavior(0)
    object Kick : IntegrationExpireBehavior(1)

    companion object Serializer : KSerializer<IntegrationExpireBehavior> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("expire_behavior", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): IntegrationExpireBehavior = when(val value = decoder.decodeInt()) {
            0 -> RemoveRole
            1 -> Kick
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: IntegrationExpireBehavior) {
            encoder.encodeInt(value.value)
        }

    }
}

@Serializable
data class DiscordIntegrationsAccount(
        val id: String,
        val name: String
)