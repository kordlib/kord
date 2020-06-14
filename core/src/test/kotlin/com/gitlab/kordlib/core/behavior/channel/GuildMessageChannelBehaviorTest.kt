package com.gitlab.kordlib.core.behavior.channel

import equality.GuildChannelEqualityTest
import mockKord

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class GuildMessageChannelBehaviorTest : GuildChannelEqualityTest<GuildMessageChannelBehavior> by GuildChannelEqualityTest({ id, guildId ->
    val kord = mockKord()
    GuildMessageChannelBehavior(id = id, guildId = guildId, kord = kord)
})