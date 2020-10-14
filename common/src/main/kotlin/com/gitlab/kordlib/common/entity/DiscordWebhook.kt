package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class DiscordWebhook(
        val id: String,
        val type: WebhookType,
        @SerialName("guild_id")
        val guildId: String? = null,
        @SerialName("channel_id")
        val channelId: String,
        val user: DiscordUser? = null,
        val name: String? = null,
        val avatar: String? = null,
        val token: String? = null
)

@Serializable(with = WebhookType.WebhookTypeSerializer::class)
enum class WebhookType(val code: Int) {
    Unknown(-1),
    Incoming(1),
    ChannelOrder(2);

    @Serializer(forClass = MessageType::class)
    companion object WebhookTypeSerializer : KSerializer<WebhookType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): WebhookType {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: WebhookType) {
            encoder.encodeInt(obj.code)
        }
    }
}