package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.common.entity.Reaction
import kotlinx.serialization.Serializable

@Serializable
data class ReactionData(
    val count: Int,
    val me: Boolean,
    val emojiId: String? = null,
    val emojiName: String,
    val emojiAnimated: Boolean
) {
    companion object {
        fun from(entity: Reaction) = with(entity) {
            ReactionData(count, me, emoji.id, emoji.name, emoji.animated ?: false)
        }
    }

}