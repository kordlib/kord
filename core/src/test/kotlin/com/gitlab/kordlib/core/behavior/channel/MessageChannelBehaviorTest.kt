package dev.kord.core.behavior.channel

import equality.ChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class MessageChannelBehaviorTest : ChannelEqualityTest<MessageChannelBehavior> by ChannelEqualityTest({ id ->
    val kord = mockKord()
    MessageChannelBehavior(id = id, kord = kord)
})