package dev.kord.rest.json.request

import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
public data class GuildFromTemplateCreateRequest(
    val name: String,
    val image: Optional<String> = Optional.Missing()
)

@Serializable
public data class GuildTemplateModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val description: Optional<String?> = Optional.Missing()
)

@Serializable
public data class GuildTemplateCreateRequest(
    val name: String,
    val description: Optional<String?> = Optional.Missing()
)
