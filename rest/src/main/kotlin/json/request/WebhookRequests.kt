package dev.kord.rest.json.request

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebhookCreateRequest(val name: String, val avatar: Optional<String> = Optional.Missing())

@Serializable
data class WebhookModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing
)

@Serializable
@OptIn(KordPreview::class)
data class WebhookExecuteRequest constructor(
    val content: Optional<String> = Optional.Missing(),
    val username: Optional<String> = Optional.Missing(),
    @SerialName("avatar_url")
    val avatar: Optional<String> = Optional.Missing(),
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>> = Optional.Missing()
)

data class MultiPartWebhookExecuteRequest(
        val request: WebhookExecuteRequest,
        val files: List<Pair<String, java.io.InputStream>> = emptyList()
)

@Serializable
@OptIn(KordPreview::class)
data class WebhookEditMessageRequest(
    val content: Optional<String?> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>> = Optional.Missing()
)

data class MultipartWebhookEditMessageRequest(
    val request: WebhookEditMessageRequest,
    val files: Optional<List<Pair<String, java.io.InputStream>>> = Optional.Missing()
)
