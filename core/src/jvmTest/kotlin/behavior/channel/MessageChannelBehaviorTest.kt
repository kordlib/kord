package dev.kord.core.behavior.channel

import dev.kord.core.equality.ChannelEqualityTest
import dev.kord.core.mockKord

internal class MessageChannelBehaviorTest : ChannelEqualityTest<MessageChannelBehavior> by ChannelEqualityTest({ id ->
    val kord = mockKord()
    MessageChannelBehavior(id = id, kord = kord)
})
