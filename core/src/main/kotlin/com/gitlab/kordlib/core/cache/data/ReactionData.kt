package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordPartialEmoji
import com.gitlab.kordlib.common.entity.Reaction
import com.gitlab.kordlib.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class ReactionData(
        val count: Int,
        val me: Boolean,
        val emojiId: Snowflake?,
        val emojiName: String?,
        val emojiAnimated: Boolean
) {
    companion object {
        fun from(entity: Reaction) = with(entity) {
            ReactionData(count, me, emoji.id, emoji.name, emoji.animated.orElse(false))
        }

        fun from(count: Int, me: Boolean, entity: DiscordPartialEmoji) = with(entity) {
            ReactionData(count, me, id, name, animated.orElse(false))
        }
    }

}