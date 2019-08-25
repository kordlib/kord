package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.`object`.ReactionEmoji
import com.gitlab.kordlib.core.`object`.data.ReactionData
import com.gitlab.kordlib.core.toSnowflakeOrNull

class Reaction(val data: ReactionData, override val kord: Kord) : KordObject {

    val id: Snowflake? get() = data.emojiId.toSnowflakeOrNull()

    val count: Int get() = data.count

    val selfReacted: Boolean get() = data.me

    val emoji: ReactionEmoji get() = when (data.emojiId) {
            null -> ReactionEmoji.Unicode(data.emojiName)
            else -> ReactionEmoji.Custom(Snowflake(data.emojiId), data.emojiName, data.emojiAnimated)
        }

    val isAnimated: Boolean get() = data.emojiAnimated


}