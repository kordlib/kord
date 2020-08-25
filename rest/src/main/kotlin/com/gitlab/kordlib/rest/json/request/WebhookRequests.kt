package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx. serialization.Serializable

@Serializable
@KordUnstableApi
data class WebhookCreateRequest(val name: String, val avatar: String?)

@Serializable
@KordUnstableApi
data class WebhookModifyRequest(
        val name: String? = null,
        val avatar: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null
)

@Serializable
@KordUnstableApi
data class WebhookExecuteRequest(
        val content: String?,
        val username: String? = null,
        @SerialName("avatar_url")
        val avatar: String? = null,
        val tts: Boolean? = null,
        val embeds: List<EmbedRequest>? = null
)

@KordUnstableApi
data class MultiPartWebhookExecuteRequest(
        val request: WebhookExecuteRequest,
        val file: Pair<String, java.io.InputStream>?
)