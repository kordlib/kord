package dev.kord.core.behavior.channel

import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class TextChannelBehaviorTest : GuildChannelEqualityTest<TextChannelBehavior> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    TextChannelBehavior(id = id, guildId = guildId, kord = kord)
})