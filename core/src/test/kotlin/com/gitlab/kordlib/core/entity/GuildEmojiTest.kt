package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.cache.data.EmojiData
import equality.GuildEntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class GuildEmojiTest : GuildEntityEqualityTest<GuildEmoji> by GuildEntityEqualityTest ({ id, guildId ->
    val data = mockk<EmojiData>()
    every { data.id } returns id.longValue
    GuildEmoji(data, guildId, mockk())
})