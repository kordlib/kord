package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class EmbedResponse(
        val enabled: Boolean,
        @SerialName("channel_id")
        val channelId: String,
)