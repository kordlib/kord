package com.gitlab.kordlib.rest.json.request

import kotlinx.io.InputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebhookCreatePostRequest(val name: String, val avatar: String?)

@Serializable
data class WebhookModifyPatchRequest(
        val name: String? = null,
        val avatar: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null
)

@Serializable
data class WebhookExecuteRequest(
        val content: String?,
        val username: String? = null,
        @SerialName("avatar_url")
        val avatar: String? = null,
        val tts: Boolean? = null,
        val embeds: List<EmbedRequest>? = null
)

data class MultiPartWebhookExecuteRequest(
        val request: WebhookExecuteRequest,
        val file: Pair<String, InputStream>?
)