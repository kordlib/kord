package com.gitlab.kordlib.core.behavior.channel

import equality.ChannelEqualityTest
import equality.GuildChannelEqualityTest
import io.mockk.mockk

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class ChannelBehaviorTest : ChannelEqualityTest<ChannelBehavior> by ChannelEqualityTest({ id ->
    ChannelBehavior(id = id, kord = mockk())
})