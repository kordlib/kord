package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.ChannelType
import kotlinx.serialization.Serializable

@Serializable
data class PartialChannelResponse(val name: String, val type: ChannelType)