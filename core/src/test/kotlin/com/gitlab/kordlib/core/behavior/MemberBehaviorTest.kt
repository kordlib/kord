package com.gitlab.kordlib.core.behavior

import equality.GuildEntityEqualityTest
import io.mockk.mockk

internal class MemberBehaviorTest : GuildEntityEqualityTest<MemberBehavior> by GuildEntityEqualityTest({ id, guildId ->
    MemberBehavior(guildId = guildId, id = id, kord = mockk())
})