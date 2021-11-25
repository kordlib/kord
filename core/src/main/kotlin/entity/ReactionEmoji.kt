package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.RemovedReactionData

public sealed class ReactionEmoji {
    /**
     * Format used in HTTP queries.
     */
    public abstract val urlFormat: String

    /**
     * Either the unicode representation if it's a [Unicode] emoji or the emoji name if it's a [Custom] emoji.
     */
    public abstract val name: String


    public abstract val mention: String

    public data class Custom(val id: Snowflake, override val name: String, val isAnimated: Boolean) : ReactionEmoji() {

        override val urlFormat: String
            get() = "$name:$id"

        override val mention: String
            get() = if (isAnimated) "<a:$name:$id>" else "<:$name:$id>"


        override fun toString(): String = "Custom(id=$id, name=$name, isAnimated=$isAnimated)"
    }

    public data class Unicode(override val name: String) : ReactionEmoji() {
        override val urlFormat: String get() = name
        override val mention: String get() = name
    }

    public companion object {
        public fun from(guildEmoji: GuildEmoji): Custom = Custom(
            guildEmoji.id, guildEmoji.name
                ?: error("emojis without name cannot be used to react"), guildEmoji.isAnimated
        )

        public fun from(guildEmoji: RemovedReactionData): ReactionEmoji = when (guildEmoji.id) {
            null -> Unicode(guildEmoji.name!!)
            else -> Custom(guildEmoji.id, guildEmoji.name!!, false)
        }
    }
}
