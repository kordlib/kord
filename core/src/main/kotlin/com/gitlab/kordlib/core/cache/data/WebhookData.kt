package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.DiscordWebhook
import com.gitlab.kordlib.common.entity.WebhookType
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class WebhookData(
        val id: Long,
        val type: WebhookType,
        val guildId: Long,
        val channelId: Long,
        val userid: Long,
        val name: String? = null,
        val avatar: String? = null,
        val token: String? = null
) {
    companion object {
        val description get() = description(WebhookData::id)

        fun from(entity: DiscordWebhook) = with(entity) { WebhookData(id.toLong(), type, guildId!!.toLong(), channelId.toLong(), user!!.id.toLong(), name, avatar, token) }
    }
}