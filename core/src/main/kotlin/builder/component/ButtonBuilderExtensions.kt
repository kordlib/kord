package dev.kord.core.builder.components

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.rest.builder.component.ButtonBuilder


public fun ButtonBuilder.emoji(emoji: ReactionEmoji) {
    this.emoji = DiscordPartialEmoji(name = emoji.name, id = null)
}


public fun ButtonBuilder.emoji(emoji: GuildEmoji) {
    this.emoji = DiscordPartialEmoji(id = emoji.id, name = null)
}
