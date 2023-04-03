package dev.kord.core.entity

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake

public sealed class Emoji {
    public abstract val name: String

    public abstract val mention: String

    public data class Guild(val id: Snowflake, override val name: String, val isAnimated: Boolean): Emoji() {
        override val mention: String
            get() = if (isAnimated) "<a:$name:$id>" else "<:$name:$id>"

        override fun toString(): String = "Guild(id=$id, name=$name, isAnimated=$isAnimated)"
    }

    public data class Standard(override val name: String): Emoji() {
        override val mention: String get() = name
    }

    public companion object {
        public fun from(guildEmoji: Guild): Guild = Guild(
                guildEmoji.id, guildEmoji.name, guildEmoji.isAnimated
        )

        public fun from(emoji: DiscordEmoji) : Emoji = when (emoji.id) {
            null -> Standard(emoji.name!!)
            else -> Guild(emoji.id!!, emoji.name!!, emoji.animated.discordBoolean)
        }
    }
}