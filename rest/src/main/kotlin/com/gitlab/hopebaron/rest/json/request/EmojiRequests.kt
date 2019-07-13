package com.gitlab.hopebaron.rest.json.request

import com.gitlab.hopebaron.common.entity.Role
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
        val roles: List<Role>? = null
)