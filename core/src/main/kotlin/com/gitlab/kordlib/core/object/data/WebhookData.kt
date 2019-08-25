package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Webhook
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebhookData(
        val id: String,
        var guildId: String,
        var channelId: String,
        var userid: String,
        var name: String? = null,
        var avatar: String? = null,
        var token: String
) {
    companion object {
        val description get() = description(WebhookData::id)

        fun from(entity: Webhook) = with(entity) { WebhookData(id, guildId!!, channelId, user!!.id, name, avatar, token) }
    }
}