package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.DiscordWebhook
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.WebhookType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.mapSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebhookData(
    val id: Snowflake,
    val type: WebhookType,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val channelId: Snowflake,
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String? = null,
    val avatar: String? = null,
    val token: Optional<String> = Optional.Missing(),
    val applicationId: Snowflake?
) {
    companion object {
        val description = description(WebhookData::id)

        fun from(entity: DiscordWebhook) = with(entity) {
            WebhookData(id, type, guildId, channelId, user.mapSnowflake { it.id }, name, avatar, token, applicationId)
        }
    }
}