@file:Generate(
    INT_KORD_ENUM, name = "WebhookType",
    docUrl = "https://discord.com/developers/docs/resources/webhook#webhook-object-webhook-types",
    entries = [
        Entry(
            "Incoming", intValue = 1,
            kDoc = "Incoming Webhooks can post messages to channels with a generated token.",
        ),
        Entry(
            "ChannelFollower", intValue = 2,
            kDoc = "Channel Follower Webhooks are internal webhooks used with Channel Following to post new messages " +
                    "into channels.",
        ),
        Entry("Application", intValue = 3, kDoc = "Application webhooks are webhooks used with Interactions."),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.map
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.*

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
) {
    override fun toString(): String = "DiscordWebhook(id=$id, type=$type, guildId=$guildId, channelId=$channelId, " +
        "user=$user, name=$name, avatar=$avatar, token=${token.map { "hunter2" }}, applicationId=$applicationId)"
}
