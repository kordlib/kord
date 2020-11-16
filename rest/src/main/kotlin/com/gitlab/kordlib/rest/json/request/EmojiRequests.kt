package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
data class EmojiCreateRequest(
        val name: String,
        val image: String,
        val roles: Set<Snowflake>
)

@Serializable
data class EmojiModifyRequest(
        val name: Optional<String> = Optional.Missing(),
        val roles: Optional<Set<Snowflake>> = Optional.Missing()
)