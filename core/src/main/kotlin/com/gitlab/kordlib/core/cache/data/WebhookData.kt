package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordUser
import com.gitlab.kordlib.common.entity.DiscordWebhook
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.WebhookType
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.mapSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebhookData(
        val id: Snowflake,
        val type: WebhookType,
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val channelId: Snowflake,
        val userId: OptionalSnowflake = OptionalSnowflake.Missing,
        val name: String?,
        val avatar: String?,
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