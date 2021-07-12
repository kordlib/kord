package dev.kord.rest.json.request

import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
data class ModifyGuildStickerRequest(
    val name: Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val tags: Optional<String> = Optional.Missing()
)
