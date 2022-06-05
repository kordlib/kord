package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.serialization.DurationInDays
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
public data class DiscordIntegration(
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
    val expireGracePeriod: DurationInDays,
    val user: DiscordUser,
    val account: DiscordIntegrationsAccount,
    @SerialName("synced_at")
    val syncedAt: Instant,
    val subscriberCount: Int,
    val revoked: Boolean,
    val application: IntegrationApplication
)

@Serializable
public data class DiscordPartialIntegration(
    val id: Snowflake,
    val name: String,
    val type: String,
    val account: DiscordIntegrationsAccount,
)

@Serializable
public data class IntegrationApplication(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val description: String,
    @Deprecated("This is deprecated and will always be empty.")
    val summary: String,
    val bot: Optional<DiscordUser> = Optional.Missing(),
)

@Serializable(with = IntegrationExpireBehavior.Serializer::class)
public sealed class IntegrationExpireBehavior(public val value: Int) {
    public class Unknown(value: Int) : IntegrationExpireBehavior(value)
    public object RemoveRole : IntegrationExpireBehavior(0)
    public object Kick : IntegrationExpireBehavior(1)

    public companion object Serializer : KSerializer<IntegrationExpireBehavior> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("expire_behavior", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): IntegrationExpireBehavior = when (val value = decoder.decodeInt()) {
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
public data class DiscordIntegrationsAccount(
    val id: String,
    val name: String
)
