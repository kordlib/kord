package dev.kord.rest.json.request

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.InputStream

@Serializable

data class ApplicationCommandCreateRequest(
    val name: String,
    val type: ApplicationCommandType,
    val description: Optional<String> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
    @SerialName("default_permission")
    val defaultPermission: OptionalBoolean = OptionalBoolean.Missing
)

@Serializable

data class ApplicationCommandModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
    @SerialName("default_permission")
    val defaultPermission: OptionalBoolean = OptionalBoolean.Missing
)

@Serializable

data class InteractionResponseModifyRequest(
    val content: Optional<String?> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>?> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions?> = Optional.Missing(),
    val flags: Optional<MessageFlags?> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>?> = Optional.Missing()
)


data class MultipartInteractionResponseModifyRequest(
    val request: InteractionResponseModifyRequest,
    val files: Optional<List<Pair<String, InputStream>>> = Optional.Missing(),
)

@Serializable

data class InteractionResponseCreateRequest(
    val type: InteractionResponseType,
    val data: Optional<InteractionApplicationCommandCallbackData> = Optional.Missing()
)


data class MultipartInteractionResponseCreateRequest(
    val request: InteractionResponseCreateRequest,
    val files: Optional<List<Pair<String, InputStream>>> = Optional.Missing()
)

@Serializable

class InteractionApplicationCommandCallbackData(
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val content: Optional<String?> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions?> = Optional.Missing(),
    val flags: Optional<MessageFlags> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>> = Optional.Missing()
)


data class MultipartFollowupMessageCreateRequest(
    val request: FollowupMessageCreateRequest,
    val files: Optional<List<Pair<String, InputStream>>> = Optional.Missing(),
)

@Serializable

class FollowupMessageCreateRequest(
    val content: Optional<String> = Optional.Missing(),
    val username: Optional<String> = Optional.Missing(),
    @SerialName("avatar_url")
    val avatar: Optional<String> = Optional.Missing(),
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>> = Optional.Missing(),
    val flags: Optional<MessageFlags> = Optional.Missing(),
)

@Serializable

data class FollowupMessageModifyRequest(
    val content: Optional<String?> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>?> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions?> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>?> = Optional.Missing()
)


data class MultipartFollowupMessageModifyRequest(
    val request: FollowupMessageModifyRequest,
    val files: Optional<List<Pair<String, InputStream>>> = Optional.Missing(),
)

@Serializable

data class ApplicationCommandPermissionsEditRequest(
        val permissions: List<DiscordGuildApplicationCommandPermission>
)
