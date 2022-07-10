package dev.kord.rest.json.response

import dev.kord.common.annotation.DeprecatedSinceKord
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.DeprecationLevel.HIDDEN

@Serializable
@DeprecatedSinceKord("0.7.0")
@Deprecated(
    "Guild embeds were renamed to widgets",
    ReplaceWith(
        "DiscordGuildWidget(enabled, channelId)",
        "dev.kord.common.entity.DiscordGuildWidget"
    ),
    level = HIDDEN,
)
public data class EmbedResponse(
    val enabled: Boolean,
    @SerialName("channel_id")
    val channelId: String
)
