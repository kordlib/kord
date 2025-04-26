package dev.kord.rest.json.response

import dev.kord.common.entity.DiscordEmoji
import kotlinx.serialization.Serializable


/**
 * List of application [emojis][DiscordEmoji].
 *
 * @property items the list of emojis
 */
@Serializable
public data class ApplicationEmojisResponse(val items: List<DiscordEmoji>)
