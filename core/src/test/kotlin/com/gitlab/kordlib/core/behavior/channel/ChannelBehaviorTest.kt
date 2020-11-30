package dev.kord.core.behavior.channel

import equality.ChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class ChannelBehaviorTest : ChannelEqualityTest<ChannelBehavior> by ChannelEqualityTest({ id ->
    val kord = mockKord()
    ChannelBehavior(id = id, kord = kord)
})