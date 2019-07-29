package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.Embed
import kotlinx.io.InputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageCreateRequest(
        val content: String,
        val nonce: String? = null,
        val tts: Boolean? = null,
        val embed: com.gitlab.kordlib.rest.json.request.EmbedRequest? = null
)

data class MultipartMessageCreateRequest(
        val request: com.gitlab.kordlib.rest.json.request.MessageCreateRequest,
        val files: List<Pair<String, InputStream>> = emptyList()
)

@Serializable
data class EmbedRequest(
        val title: String?,
        val type: String?,
        val description: String?,
        val url: String?,
        val timestamp: String? = null,
        val color: Int? = null,
        val footer: com.gitlab.kordlib.rest.json.request.EmbedFooterRequest? = null,
        val image: com.gitlab.kordlib.rest.json.request.EmbedImageRequest? = null,
        val thumbnail: com.gitlab.kordlib.rest.json.request.EmbedThumbnailRequest? = null,
        val author: com.gitlab.kordlib.rest.json.request.EmbedAuthorRequest? = null,
        val fields: List<com.gitlab.kordlib.rest.json.request.EmbedFieldRequest>? = null
)


@Serializable
data class EmbedFooterRequest(
        val text: String,
        val url: String? = null,
        @SerialName("icon_url")
        val iconUrl: String? = null
)

@Serializable
data class EmbedImageRequest(val url: String)

@Serializable
data class EmbedThumbnailRequest(val url: String)

@Serializable
data class EmbedAuthorRequest(
        val name: String? = null,
        val url: String? = null,
        @SerialName("icon_url")
        val iconUrl: String? = null
)

@Serializable
data class EmbedFieldRequest(
        val name: String,
        val value: String,
        val inline: Boolean? = null
)

@Serializable
data class MessageEditRequest(
        val content: String? = null,
        val embed: Embed? = null
)

@Serializable
data class BulkDeleteRequest(val messages: List<String>)
