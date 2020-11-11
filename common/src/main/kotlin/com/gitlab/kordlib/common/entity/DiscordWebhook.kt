package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class DiscordWebhook(
        val id: Snowflake,
        val type: WebhookType,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("channel_id")
        val channelId: Snowflake,
        val user: Optional<DiscordUser> = Optional.Missing(),
        val name: String?,
        val avatar: String?,
        val token: Optional<String> = Optional.Missing(),
        @SerialName("application_id")
        val applicationId: Snowflake?,
)

@Serializable(with = WebhookType.Serializer::class)
sealed class WebhookType(val value: Int) {
    class Unknown(value: Int): WebhookType(value)
    object Incoming: WebhookType(1)
    object ChannelFollower: WebhookType(2)

    internal object Serializer : KSerializer<WebhookType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): WebhookType = when(val value = decoder.decodeInt()) {
            0 -> Incoming
            2 -> ChannelFollower
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: WebhookType) {
            encoder.encodeInt(value.value)
        }
    }
}