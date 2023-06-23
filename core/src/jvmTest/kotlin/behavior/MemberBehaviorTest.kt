package dev.kord.core.behavior

import dev.kord.core.equality.GuildEntityEqualityTest
import dev.kord.core.mockKord

internal class MemberBehaviorTest : GuildEntityEqualityTest<MemberBehavior> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    MemberBehavior(guildId = guildId, id = id, kord = kord)
})
