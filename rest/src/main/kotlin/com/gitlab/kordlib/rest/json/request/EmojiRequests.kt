package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.Role
import kotlinx.serialization.Serializable

@Serializable
data class EmojiCreatePostRequest(
        val name: String,
        val image: String,
        val roles: List<String>
)

@Serializable
data class EmojiModifyPatchRequest(
        val name: String? = null,
        val roles: List<Role>? = null
)