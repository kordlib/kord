package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.Embed
import kotlinx.io.InputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateWebhookRequest(val name: String, val avatar: String?)

@Serializable
data class ModifyWebhookRequest(
        val name: String? = null,
        val avatar: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null
)

@Serializable
data class WebhookExecuteRequest(
        val content: String,
        val username: String? = null,
        @SerialName("avatar_url")
        val avatar: String? = null,
        val tts: Boolean? = null,
        val embeds: List<Embed>? = null,
        @SerialName("payload_json")
        val payload: String? = null
)

data class MultiPartWebhookExecuteRequest(
        val request: com.gitlab.kordlib.rest.json.request.WebhookExecuteRequest,
        val files: List<Pair<String, InputStream>>)