package dev.kord.rest.json.request

import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.InputStream

@Serializable
data class GlobalApplicationCommandCreateRequest(
    val name: String,
    val description: String,
    val options: Optional<List<ApplicationCommandOption>>
)

@Serializable
data class GlobalApplicationCommandModifyRequest(
    val name: Optional<String>,
    val description: Optional<String>,
    val options: Optional<List<ApplicationCommandOption>>
)

@Serializable
data class GuildApplicationCommandCreateRequest(
    val name: String,
    val description: String,
    val options: Optional<List<ApplicationCommandOption>>
)

@Serializable
data class GuildApplicationCommandModifyRequest(
    val name: Optional<String>,
    val description: Optional<String>,
    val options: Optional<List<ApplicationCommandOption>>
)

@Serializable
data class OriginalInteractionResponseModifyRequest(
    val content: Optional<String>,
    val embeds: Optional<List<EmbedRequest>>,
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions>,
)


@Serializable
data class InteractionResponseCreateRequest(
    val type: InteractionResponseType,
    val data: Optional<InteractionApplicationCommandCallbackData> = Optional.Missing()
)

@Serializable
class InteractionApplicationCommandCallbackData(
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val content: String,
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing()

)
data class MultipartFollowupMessageCreateRequest(
    val request: FollowupMessageCreateRequest,
    val file: Pair<String, InputStream>?
)

@Serializable
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
data class FollowupMessageModifyRequest(
    val content: Optional<String>,
    val embeds: Optional<List<EmbedRequest>>,
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions>,
)