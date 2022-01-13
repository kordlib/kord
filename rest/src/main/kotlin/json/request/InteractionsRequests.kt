package dev.kord.rest.json.request

import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.DiscordAutoComplete
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordGuildApplicationCommandPermission
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.rest.NamedFile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class ApplicationCommandCreateRequest(
    val name: String,
    val type: ApplicationCommandType,
    val description: Optional<String> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
    @SerialName("default_permission")
    val defaultPermission: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("dm_permissions")
    val dmPermissions: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("default_member_permissions")
    val defaultMemberPermissions: Optional<Permissions> = Optional.Missing(),
)

@Serializable

data class ApplicationCommandModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing(),
    @SerialName("default_permission")
    val defaultPermission: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("dm_permissions")
    val dmPermissions: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("default_member_permissions")
    val defaultMemberPermissions: Optional<Permissions> = Optional.Missing(),
)

@Serializable

data class InteractionResponseModifyRequest(
    val content: Optional<String?> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>?> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions?> = Optional.Missing(),
    val flags: Optional<MessageFlags?> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>?> = Optional.Missing(),
    val attachments: Optional<MutableList<DiscordAttachment>?> = Optional.Missing()
)


data class MultipartInteractionResponseModifyRequest(
    val request: InteractionResponseModifyRequest,
    val files: Optional<List<NamedFile>> = Optional.Missing(),
)

@Serializable
data class InteractionResponseCreateRequest(
    val type: InteractionResponseType,
    val data: Optional<InteractionApplicationCommandCallbackData> = Optional.Missing()
)

@Serializable
data class AutoCompleteResponseCreateRequest<T>(
    val type: InteractionResponseType,
    val data: DiscordAutoComplete<T>
)


data class MultipartInteractionResponseCreateRequest(
    val request: InteractionResponseCreateRequest,
    val files: Optional<List<NamedFile>> = Optional.Missing()
)

@Serializable

class InteractionApplicationCommandCallbackData(
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val content: Optional<String?> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions?> = Optional.Missing(),
    val flags: Optional<MessageFlags> = Optional.Missing(),
    val components: Optional<List<DiscordComponent>> = Optional.Missing(),
)


data class MultipartFollowupMessageCreateRequest(
    val request: FollowupMessageCreateRequest,
    val files: List<NamedFile> = emptyList(),
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
    val components: Optional<List<DiscordComponent>?> = Optional.Missing(),
    val attachments: Optional<List<DiscordAttachment>?> = Optional.Missing()
)


data class MultipartFollowupMessageModifyRequest(
    val request: FollowupMessageModifyRequest,
    val files: Optional<List<NamedFile>> = Optional.Missing()
)

@Serializable

data class ApplicationCommandPermissionsEditRequest(
    val permissions: List<DiscordGuildApplicationCommandPermission>
)
