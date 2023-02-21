package dev.kord.core.builder.components

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.optional
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.rest.builder.component.ButtonBuilder

/**
 * Sets the emoji that appears on the button.
 *
 * @param emoji The [ReactionEmoji.Unicode] for the emoji
 */
public fun ButtonBuilder.emoji(emoji: ReactionEmoji.Unicode) {
    this.emoji = DiscordPartialEmoji(name = emoji.name, id = null)
}

/**
 * Sets the emoji that appears on the button.
 *
 * @param emoji The [ReactionEmoji.Custom] for the emoji
 */
public fun ButtonBuilder.emoji(emoji: ReactionEmoji.Custom) {
    this.emoji = DiscordPartialEmoji(name = emoji.name, id = emoji.id, animated = emoji.isAnimated.optional())
}

/**
 * Sets the emoji that appears on the button.
 *
 * @param emoji The [GuildEmoji] for the emoji
 */
public fun ButtonBuilder.emoji(emoji: GuildEmoji) {
    this.emoji = DiscordPartialEmoji(id = emoji.id, name = null, animated = emoji.isAnimated.optional())
}
