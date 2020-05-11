package com.gitlab.kordlib.core.behavior.channel

import equality.ChannelEqualityTest
import io.mockk.mockk

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class MessageChannelBehaviorTest : ChannelEqualityTest<MessageChannelBehavior> by ChannelEqualityTest({ id ->
    MessageChannelBehavior(id = id, kord = mockk())
})