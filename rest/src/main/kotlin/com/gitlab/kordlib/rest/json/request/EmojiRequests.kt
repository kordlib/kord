package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class EmojiCreateRequest(
        val name: String,
        val image: String,
        val roles: List<String>
)

@Serializable
@KordUnstableApi
data class EmojiModifyRequest(
        val name: String? = null,
        val roles: List<String>? = null
)