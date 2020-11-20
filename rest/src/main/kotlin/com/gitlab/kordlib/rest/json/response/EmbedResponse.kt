package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@DeprecatedSinceKord("0.7.0")
@Deprecated(
        "Guild embeds were renamed to widgets",
        ReplaceWith(
                "DiscordGuildWidget(enabled, channelId)",
                "com.gitlab.kordlib.common.entity.DiscordGuildWidget"
        ),
        DeprecationLevel.ERROR
)
data class EmbedResponse(
        val enabled: Boolean,
        @SerialName("channel_id")
        val channelId: String
)