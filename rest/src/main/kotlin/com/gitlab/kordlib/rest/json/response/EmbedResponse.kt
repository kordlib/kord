package com.gitlab.kordlib.rest.json.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmbedResponse(val enabled: Boolean,
                         @SerialName("channel_id")
                         val channelId: String)