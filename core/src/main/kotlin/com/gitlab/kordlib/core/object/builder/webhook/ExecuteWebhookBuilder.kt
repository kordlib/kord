package com.gitlab.kordlib.core.`object`.builder.webhook

import com.gitlab.kordlib.common.entity.Embed
import com.gitlab.kordlib.core.`object`.builder.message.EmbedBuilder
import com.gitlab.kordlib.rest.json.request.EmbedRequest
import com.gitlab.kordlib.rest.json.request.MultiPartWebhookExecuteRequest
import com.gitlab.kordlib.rest.json.request.WebhookExecuteRequest
import com.gitlab.kordlib.rest.route.Route
import kotlinx.io.InputStream

class ExecuteWebhookBuilder (
        var content: String? = null,
        var username: String? = null,
        var avatarUrl: String? = null,
        var tts: Boolean? = null,
        private var file: Pair<String, InputStream>? = null,
        val embeds: MutableList<EmbedRequest> = mutableListOf()
) {
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        embeds += EmbedBuilder().apply(builder).toRequest()
    }

    fun toRequest() : MultiPartWebhookExecuteRequest = MultiPartWebhookExecuteRequest(
        WebhookExecuteRequest(content, username, avatarUrl, tts, embeds), file
    )
}