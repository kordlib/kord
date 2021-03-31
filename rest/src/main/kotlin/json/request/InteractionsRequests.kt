package dev.kord.rest.json.request

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.InputStream

@Serializable
@KordPreview
data class ApplicationCommandCreateRequest(
    val name: String,
    val description: String,
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing()
)

@Serializable
@KordPreview
data class ApplicationCommandModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing()
)

@Serializable
@KordPreview
data class InteractionResponseModifyRequest(
    val content: Optional<String> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing() ,
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
)

@KordPreview
data class MultipartInteractionResponseModifyRequest(
    val request: InteractionResponseModifyRequest,
    val files: List<Pair<String, java.io.InputStream>> = emptyList(),
)

@Serializable
@KordPreview
data class InteractionResponseCreateRequest(
    val type: InteractionResponseType,
    val data: Optional<InteractionApplicationCommandCallbackData> = Optional.Missing()
)

@Serializable
@KordPreview
class InteractionApplicationCommandCallbackData(
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val content: Optional<String> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
    val flags: Optional<MessageFlags> = Optional.Missing()

)
@KordPreview
data class MultipartFollowupMessageCreateRequest(
    val request: FollowupMessageCreateRequest,
    val file: Pair<String, InputStream>?
)

@Serializable
@KordPreview
class FollowupMessageCreateRequest(
    val content: Optional<String> = Optional.Missing(),
    val username: Optional<String> = Optional.Missing(),
    @SerialName("avatar_url")
    val avatar: Optional<String> = Optional.Missing(),
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing()
)

@Serializable
@KordPreview
data class FollowupMessageModifyRequest(
    val content: Optional<String> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
)