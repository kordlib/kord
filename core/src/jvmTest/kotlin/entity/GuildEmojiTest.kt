package dev.kord.core.entity

import dev.kord.core.cache.data.EmojiData
import dev.kord.core.equality.GuildEntityEqualityTest
import dev.kord.core.mockKord
import io.mockk.every
import io.mockk.mockk

internal class GuildEmojiTest : GuildEntityEqualityTest<GuildEmoji> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    val data = mockk<EmojiData>()
    every { data.id } returns id
    every { data.guildId } returns guildId
    GuildEmoji(data, kord)
})
