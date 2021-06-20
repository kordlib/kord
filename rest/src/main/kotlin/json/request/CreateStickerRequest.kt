package dev.kord.rest.json.request

import dev.kord.common.entity.optional.Optional
import io.ktor.client.request.forms.InputProvider

data class CreateStickerRequest(
    val name: String,
    val description: Optional<String> = Optional.Missing(),
    val tags: String,
    val file: InputProvider
)
