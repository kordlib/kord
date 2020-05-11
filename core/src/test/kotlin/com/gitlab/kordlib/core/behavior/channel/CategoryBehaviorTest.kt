package com.gitlab.kordlib.core.behavior.channel

import equality.GuildChannelEqualityTest
import io.mockk.mockk

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class CategoryBehaviorTest : GuildChannelEqualityTest<CategoryBehavior> by GuildChannelEqualityTest({ id, guildId ->
    CategoryBehavior(id = id, guildId = guildId, kord = mockk())
})