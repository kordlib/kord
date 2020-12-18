package dev.kord.rest.json.request

import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.ApplicationCommandOption
import dev.kord.common.entity.DiscordEmbed
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
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
    val content: String,
    val embeds: List<DiscordEmbed>,
    @SerialName("allowed_mentions")
    val allowedMentions: AllowedMentions,
)
