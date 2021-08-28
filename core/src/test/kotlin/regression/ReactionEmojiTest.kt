package regression

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.ReactionEmoji
import kotlin.test.Test

class ReactionEmojiTest {

    @Test
    fun `getting the id of a reaction emoji doesn't cause a castException`() {
        val emoji = ReactionEmoji.Custom(Snowflake(0u), "test", false)
        emoji.id
    }
}
