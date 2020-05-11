package com.gitlab.kordlib.core.behavior.channel

import equality.GuildChannelEqualityTest
import io.mockk.mockk

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class GuildMessageChannelBehaviorTest : GuildChannelEqualityTest<GuildMessageChannelBehavior> by GuildChannelEqualityTest({ id, guildId ->
    GuildMessageChannelBehavior(id = id, guildId = guildId, kord = mockk())
})