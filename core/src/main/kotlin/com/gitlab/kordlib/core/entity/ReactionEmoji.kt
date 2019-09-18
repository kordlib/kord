package com.gitlab.kordlib.core.entity

sealed class ReactionEmoji {
    abstract val formatted: String

    data class Custom(val id: Snowflake, val name: String, val isAnimated: Boolean) : ReactionEmoji() {
        override val formatted: String
            get() = "$name:${id.value}"
    }

    data class Unicode(val raw: String) : ReactionEmoji() {
        override val formatted: String get() = raw
    }

    companion object {
        fun from(guildEmoji: GuildEmoji) = Custom(guildEmoji.id, guildEmoji.name, guildEmoji.isAnimated)
    }
}