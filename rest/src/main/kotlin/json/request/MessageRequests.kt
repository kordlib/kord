package dev.kord.rest.json.request

import dev.kord.common.Color
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.rest.NamedFile
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class MessageCreateRequest(
    val content: Optional<String> = Optional.Missing(),
    val nonce: Optional<String> = Optional.Missing(),
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
    @SerialName("message_reference")
    val messageReference: Optional<DiscordMessageReference> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>> = Optional.Missing()
)

public data class MultipartMessageCreateRequest(
    val request: MessageCreateRequest,
    val files: List<NamedFile> = emptyList(),
)

@Serializable
public data class EmbedRequest(
    val title: Optional<String> = Optional.Missing(),
    val type: Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    val timestamp: Optional<Instant> = Optional.Missing(),
    val color: Optional<Color> = Optional.Missing(),
    val footer: Optional<EmbedFooterRequest> = Optional.Missing(),
    val image: Optional<EmbedImageRequest> = Optional.Missing(),
    val thumbnail: Optional<EmbedThumbnailRequest> = Optional.Missing(),
    val author: Optional<EmbedAuthorRequest> = Optional.Missing(),
    val fields: Optional<List<EmbedFieldRequest>> = Optional.Missing(),
)

@Serializable
public data class EmbedFooterRequest(
    val text: String,
    @SerialName("icon_url")
    val iconUrl: String? = null,
)

@Serializable
public data class EmbedImageRequest(val url: String)

@Serializable
public data class EmbedThumbnailRequest(val url: String)

@Serializable
public data class EmbedAuthorRequest(
    val name: Optional<String> = Optional.Missing(),
    val url: Optional<String> = Optional.Missing(),
    @SerialName("icon_url")
    val iconUrl: Optional<String> = Optional.Missing(),
)

@Serializable
public data class EmbedFieldRequest(
    val name: String,
    val value: String,
    val inline: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
public data class MessageEditPatchRequest(
    val content: Optional<String?> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>?> = Optional.Missing(),
    val flags: Optional<MessageFlags?> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions?> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>> = Optional.Missing(),
    val attachments: Optional<MutableList<DiscordAttachment>> = Optional.Missing()
)

public data class MultipartMessagePatchRequest(
    val requests: MessageEditPatchRequest,
    val files: Optional<List<NamedFile>> = Optional.Missing()
)

@Serializable
public data class BulkDeleteRequest(val messages: List<Snowflake>)
