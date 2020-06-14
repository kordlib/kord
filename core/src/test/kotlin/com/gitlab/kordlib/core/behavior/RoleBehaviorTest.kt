package com.gitlab.kordlib.core.behavior

import equality.GuildEntityEqualityTest
import mockKord

internal class RoleBehaviorTest : GuildEntityEqualityTest<RoleBehavior> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    RoleBehavior(guildId = guildId, id = id, kord = kord)
})