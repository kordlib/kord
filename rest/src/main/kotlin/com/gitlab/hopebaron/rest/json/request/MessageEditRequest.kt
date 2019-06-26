package com.gitlab.hopebaron.rest.json.request

import com.gitlab.hopebaron.common.entity.Embed
import kotlinx.serialization.Serializable

@Serializable
data class MessageEditRequest(val content: String? = null,
                              val embed: Embed? = null)