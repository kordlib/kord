package dev.kord.core.builder.components

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.optional
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.rest.builder.component.ButtonBuilder


public fun ButtonBuilder.emoji(emoji: ReactionEmoji.Unicode) {
    this.emoji = DiscordPartialEmoji(name = emoji.name, id = null)
}

public fun ButtonBuilder.emoji(emoji: ReactionEmoji.Custom) {
    this.emoji = DiscordPartialEmoji(name = emoji.name, id = emoji.id, animated = emoji.isAnimated.optional())
}

public fun ButtonBuilder.emoji(emoji: GuildEmoji) {
    this.emoji = DiscordPartialEmoji(id = emoji.id, name = null, animated = emoji.isAnimated.optional())
}
