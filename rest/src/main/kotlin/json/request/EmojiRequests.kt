package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
public data class EmojiCreateRequest(
    val name: String,
    val image: String,
    val roles: Set<Snowflake>
)

@Serializable
public data class EmojiModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val roles: Optional<Set<Snowflake>?> = Optional.Missing()
)
