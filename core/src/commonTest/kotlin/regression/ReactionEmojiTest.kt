package dev.kord.core.regression

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.ReactionEmoji
import kotlin.js.JsName
import kotlin.test.Test

class ReactionEmojiTest {

    @Test
    @JsName("test1")
    fun `getting the id of a reaction emoji doesn't cause a castException`() {
        val emoji = ReactionEmoji.Custom(Snowflake(0u), "test", false)
        emoji.id
    }
}
