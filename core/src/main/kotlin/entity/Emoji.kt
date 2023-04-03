package dev.kord.core.entity

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake

public sealed class Emoji {
    /**
     * Either the unicode representation if it's a [Standard] emoji, or the emoji name if it's a [Guild] emoji.
     */
    public abstract val name: String

    /**
     * Either the mention string formatted as <:name:id> if it's a [Guild] emoji or the name if it's a [Standard] emoji.
     */
    public abstract val mention: String

    /**
     * Representation of a custom [GuildEmoji].
     *
     * @param id The ID of the emoji
     * @param name Either the unicode representation if it's a [Standard] emoji, or the emoji name if it's a [Guild] emoji.
     * @param isAnimated Whether the emoji is animated
     */
    public data class Guild(val id: Snowflake, override val name: String, val isAnimated: Boolean): Emoji() {
        override val mention: String
            get() = if (isAnimated) "<a:$name:$id>" else "<:$name:$id>"

        override fun toString(): String = "Guild(id=$id, name=$name, isAnimated=$isAnimated)"
    }

    /**
     * Representation of a [Standard] discord emoji
     * @param name Either the unicode representation if it's a [Standard] emoji, or the emoji name if it's a [Guild] emoji.
     */
    public data class Standard(override val name: String): Emoji() {
        override val mention: String get() = name
    }

    public companion object {
        /** Converts a [GuildEmoji] to a [Guild] emoji. */
        public fun from(guildEmoji: GuildEmoji): Guild = Guild(
                guildEmoji.id, guildEmoji.name ?: "", guildEmoji.isAnimated
        )

        /** Converts a [DiscordEmoji] object to the correct [Emoji] type. */
        public fun from(emoji: DiscordEmoji) : Emoji = when (emoji.id) {
            null -> Standard(emoji.name!!)
            else -> Guild(emoji.id!!, emoji.name!!, emoji.animated.discordBoolean)
        }
    }
}