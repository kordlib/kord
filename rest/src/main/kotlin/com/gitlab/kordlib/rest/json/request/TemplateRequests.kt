package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
data class GuildTemplateCreateRequest(
        val name: String,
        val image: Optional<String> = Optional.Missing()
)