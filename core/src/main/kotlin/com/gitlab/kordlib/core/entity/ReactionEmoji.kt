package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.cache.data.RemovedReactionData

sealed class ReactionEmoji {
    abstract val formatted: String
    abstract val name: String
    abstract val id: Snowflake?

    data class Custom(override val id: Snowflake, override val name: String, val isAnimated: Boolean) : ReactionEmoji() {
        override val formatted: String
            get() = "$name:${id.value}"


        override fun toString() = "Custom(id=$id, name=$name, isAnimated=$isAnimated)"
    }

    class Unicode(override val name: String) : ReactionEmoji() {
        override val id: Snowflake? get() = null
        override val formatted: String get() = name
    }

    companion object {
        fun from(guildEmoji: GuildEmoji) = Custom(guildEmoji.id, guildEmoji.name ?: error("emojis without name cannot be used to react"), guildEmoji.isAnimated)

        fun from(guildEmoji: RemovedReactionData) = when(guildEmoji.id) {
            null -> Unicode(guildEmoji.name)
            else -> Custom(Snowflake(guildEmoji.id), guildEmoji.name, false)
        }
    }
}