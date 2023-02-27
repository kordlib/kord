package dev.kord.core.behavior

import equality.GuildEntityEqualityTest
import mockKord

internal class MemberBehaviorTest : GuildEntityEqualityTest<MemberBehavior> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    MemberBehavior(guildId = guildId, id = id, kord = kord)
})