package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Reaction
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class ReactionData(
    val count: Int,
    val me: Boolean,
    val emojiId: Snowflake? = null,
    val emojiName: String? = null,
    val emojiAnimated: Boolean
) {
    public companion object {
        public fun from(entity: Reaction): ReactionData = with(entity) {
            ReactionData(count, me, emoji.id, emoji.name, emoji.animated.orElse(false))
        }

        public fun from(count: Int, me: Boolean, entity: DiscordPartialEmoji): ReactionData = with(entity) {
            ReactionData(count, me, id, name, animated.orElse(false))
        }
    }
}
