package dev.kord.rest.json.request

import dev.kord.common.entity.optional.Optional
import dev.kord.rest.NamedFile
import kotlinx.serialization.Serializable

@Serializable
data class GuildStickerCreateRequest(
val name: String,
val description: String,
val tags: String,
)

data class  MultipartGuildStickerCreateRequest(
    val request: GuildStickerCreateRequest,
    val file: NamedFile
)

@Serializable
data class GuildStickerModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val tags: Optional<String> = Optional.Missing(),
)