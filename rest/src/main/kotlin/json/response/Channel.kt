package dev.kord.rest.json.response

import dev.kord.common.entity.ChannelType
import kotlinx.serialization.Serializable

@Serializable
data class PartialChannelResponse(val name: String, val type: ChannelType)