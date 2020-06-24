package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.cache.data.RemovedReactionData

sealed class ReactionEmoji {
    abstract val urlFormat: String
    abstract val name: String
    abstract val mention: String

    data class Custom(val id: Snowflake, override val name: String, val isAnimated: Boolean) : ReactionEmoji() {
        /**
         *
         * Format used in HTTP queries.
         *
         */
        override val urlFormat: String
            get() = "$name:${id.value}"

        override val mention: String
            get() = if (isAnimated) "<a:$name:${id.value}>" else "<:$name:${id.value}>"


        override fun toString() = "Custom(id=$id, name=$name, isAnimated=$isAnimated)"
    }

    class Unicode(override val name: String) : ReactionEmoji() {
        override val urlFormat: String get() = name
        override val mention: String get() = name
    }

    companion object {
        fun from(guildEmoji: GuildEmoji) = Custom(guildEmoji.id, guildEmoji.name
                ?: error("emojis without name cannot be used to react"), guildEmoji.isAnimated)

        fun from(guildEmoji: RemovedReactionData) = when (guildEmoji.id) {
            null -> Unicode(guildEmoji.name)
            else -> Custom(Snowflake(guildEmoji.id), guildEmoji.name, false)
        }
    }
}