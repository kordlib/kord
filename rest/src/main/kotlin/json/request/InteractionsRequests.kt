package dev.kord.rest.json.request

import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlobalApplicationCommandCreateRequest(
    val name: String,
    val description: String,
    val options: Optional<List<ApplicationCommandOption>>
)

@Serializable
data class GlobalApplicationCommandModifyRequest(
    val name: String,
    val description: String,
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
    val name: String,
    val description: String,
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
data class DiscordInteractionResponse(
    val type: InteractionResponseType,
    val data: Optional<DiscordInteractionApplicationCommandCallbackData> = Optional.Missing()
)

@Serializable
class DiscordInteractionApplicationCommandCallbackData(
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val content: String,
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing()

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