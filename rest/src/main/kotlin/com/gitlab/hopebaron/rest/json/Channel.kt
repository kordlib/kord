package com.gitlab.hopebaron.rest.json

import com.gitlab.hopebaron.common.entity.ChannelType
import kotlinx.serialization.Serializable

@Serializable
data class PartialChannel(val name: String, val type: ChannelType)