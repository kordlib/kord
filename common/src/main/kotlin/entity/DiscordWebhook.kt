package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A representation of the [Discord Webhook structure](https://discord.com/developers/docs/resources/webhook#webhook-object).
 *
 * @param id The id of the webhook.
 * @param type The type of the webhook.
 * @param guildId The guild id this webhook is for.
 * @param channelId The channel id this webhook is for.
 * @param user The user this webhook was created by (not present when getting a webhook with its [token]).
 * @param name The default name of the webhook.
 * @param avatar The default avatar of the webhook.
 * @param token The secure token of this webhook (returned for [incoming webhooks][WebhookType.Incoming]).
 * @param applicationId The bot/OAuth2 application that created this webhook.
 */
@Serializable
public data class DiscordWebhook(
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
public sealed class WebhookType(public val value: Int) {
    public class Unknown(value: Int) : WebhookType(value)

    /**
     * Incoming Webhooks can post messages to channels with a generated token.
     */
    public object Incoming : WebhookType(1)

    /**
     * 	Channel Follower Webhooks are internal webhooks used with Channel Following to post new messages into channels.
     */
    public object ChannelFollower : WebhookType(2)

    internal object Serializer : KSerializer<WebhookType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): WebhookType = when (val value = decoder.decodeInt()) {
            1 -> Incoming
            2 -> ChannelFollower
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: WebhookType) {
            encoder.encodeInt(value.value)
        }
    }
}
