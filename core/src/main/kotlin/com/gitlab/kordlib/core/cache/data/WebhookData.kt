package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Webhook
import kotlinx.serialization.Serializable

@Serializable
data class WebhookData(
        val id: String,
        val guildId: String,
        val channelId: String,
        val userid: String,
        val name: String? = null,
        val avatar: String? = null,
        val token: String
) {
    companion object {
        val description get() = description(WebhookData::id)

        fun from(entity: Webhook) = with(entity) { WebhookData(id, guildId!!, channelId, user!!.id, name, avatar, token) }
    }
}