package com.gitlab.hopebaron.rest.json.request

import com.gitlab.hopebaron.common.entity.Embed
import com.gitlab.hopebaron.common.entity.Overwrite
import com.gitlab.hopebaron.common.entity.Snowflake
import kotlinx.io.InputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageCreateRequest(
        val content: String,
        val nonce: Snowflake? = null,
        val tts: Boolean? = null,
        val embed: EmbedRequest? = null
)

data class MultipartMessageCreateRequest(
        val request: MessageCreateRequest,
        val files: List<Pair<String, InputStream>> = emptyList()
)

@Serializable
data class EmbedRequest(
        val title: String?,
        val type: String?,
        val description: String?,
        val url: String?,
        val timestamp: String,
        val color: Int,
        val footer: EmbedFooterRequest,
        val image: EmbedImageRequest,
        val thumbnail: EmbedThumbnailRequest,
        val author: EmbedAuthorRequest,
        val fields: List<EmbedFieldRequest>
)


@Serializable
data class EmbedFooterRequest(
        val text: String,
        val url: String,
        @SerialName("icon_url")
        val iconUrl: String
)

@Serializable
data class EmbedImageRequest(val url: String)

@Serializable
data class EmbedThumbnailRequest(val url: String)

@Serializable
data class EmbedAuthorRequest(
        val name: String,
        val url: String,
        @SerialName("icon_url")
        val iconUrl: String
)

@Serializable
data class EmbedFieldRequest(
        val name: String,
        val value: String,
        val inline: Boolean
)

@Serializable
data class MessageEditRequest(val content: String? = null,
                              val embed: Embed? = null)

@Serializable
data class PutModifyMessageRequest(val name: String,
                                   val position: Int,
                                   val topic: String,
                                   val nsfw: Boolean,
                                   @SerialName("rate_limit_per_user")
                                   val rateLimitPerUser: Int,
                                   val bitrate: Int,
                                   @SerialName("user_limit")
                                   val userLimit: Int,
                                   @SerialName("permission_overwrites")
                                   val permissionOverwrites: List<Overwrite>,
                                   @SerialName("parent_id")
                                   val parentId: Snowflake)

@Serializable
data class PatchModifyMessageRequest(val name: String? = null,
                                     val position: Int? = null,
                                     val topic: String? = null,
                                     val nsfw: Boolean? = null,
                                     @SerialName("rate_limit_per_user")
                                     val rateLimitPerUser: Int? = null,
                                     val bitrate: Int? = null,
                                     @SerialName("user_limit")
                                     val userLimit: Int? = null,
                                     @SerialName("permission_overwrites")
                                     val permissionOverwrites: List<Overwrite>? = null,
                                     @SerialName("parent_id")
                                     val parentId: Snowflake? = null)

@Serializable
data class BulkDeleteRequest(val messages: List<String>)
