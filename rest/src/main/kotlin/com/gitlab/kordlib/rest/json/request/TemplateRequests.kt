package dev.kord.rest.json.request

import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
data class GuildTemplateCreateRequest(
        val name: String,
        val image: Optional<String> = Optional.Missing()
)