package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Reaction
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class ReactionData(
    val count: Int,
    val me: Boolean,
    val emojiId: Snowflake? = null,
    val emojiName: String? = null,
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