package com.gitlab.kordlib.core.`object`.builder.webhook

import com.gitlab.kordlib.common.entity.Embed
import com.gitlab.kordlib.core.`object`.builder.RequestBuilder
import com.gitlab.kordlib.core.`object`.builder.message.EmbedBuilder
import com.gitlab.kordlib.rest.json.request.EmbedRequest
import com.gitlab.kordlib.rest.json.request.MultiPartWebhookExecuteRequest
import com.gitlab.kordlib.rest.json.request.WebhookExecuteRequest
import com.gitlab.kordlib.rest.route.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class ExecuteWebhookBuilder: RequestBuilder<MultiPartWebhookExecuteRequest> {
    var content: String? = null
    var username: String? = null
    var avatarUrl: String? = null
    var tts: Boolean? = null
    private var file: Pair<String, InputStream>? = null
    val embeds: MutableList<EmbedRequest> = mutableListOf()

    fun setFile(name: String, content: java.io.InputStream) {
        file = name to content
    }

    suspend fun setFile(path: Path) = withContext(Dispatchers.IO) {
        setFile(path.fileName.toString(), Files.newInputStream(path))
    }

    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        embeds += EmbedBuilder().apply(builder).toRequest()
    }

    override fun toRequest() : MultiPartWebhookExecuteRequest = MultiPartWebhookExecuteRequest(
        WebhookExecuteRequest(content, username, avatarUrl, tts, embeds), file
    )
}