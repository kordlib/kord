package regression

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.ReactionEmoji
import kotlin.test.Test

class ReactionEmojiTest {

    @Test
    fun `getting the id of a reaction emoji doesn't cause a castException`() {
        val emoji = ReactionEmoji.Custom(Snowflake(0), "test", false)
        emoji.id
    }
}
