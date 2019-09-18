package com.gitlab.kordlib.rest.json.request

import kotlinx.serialization.Serializable

@Serializable
data class EmojiCreateRequest(
        val name: String,
        val image: String,
        val roles: List<String>
)

@Serializable
data class EmojiModifyRequest(
        val name: String? = null,
        val roles: List<String>? = null
)