package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordPartialEmoji
import com.gitlab.kordlib.common.entity.Reaction
import kotlinx.serialization.Serializable

@Serializable
data class ReactionData(
    val count: Int,
    val me: Boolean,
    val emojiId: Long? = null,
    val emojiName: String,
    val emojiAnimated: Boolean
) {
    companion object {
        fun from(entity: Reaction) = with(entity) {
            ReactionData(count, me, emoji.id?.toLong(), emoji.name, emoji.animated ?: false)
        }

        fun from(count: Int, me: Boolean, entity: DiscordPartialEmoji) = with(entity) {
            ReactionData(count, me, id?.toLong(), name, animated ?: false)
        }
    }

}