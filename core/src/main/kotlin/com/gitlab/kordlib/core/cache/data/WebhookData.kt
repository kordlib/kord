package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Webhook
import kotlinx.serialization.Serializable

@Serializable
data class WebhookData(
        val id: Long,
        val guildId: Long,
        val channelId: Long,
        val userid: Long,
        val name: String? = null,
        val avatar: String? = null,
        val token: String
) {
    companion object {
        val description get() = description(WebhookData::id)

        fun from(entity: Webhook) = with(entity) { WebhookData(id.toLong(), guildId!!.toLong(), channelId.toLong(), user!!.id.toLong(), name, avatar, token) }
    }
}