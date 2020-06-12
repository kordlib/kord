package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.cache.data.RoleData
import equality.EntityEqualityTest
import equality.GuildEntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class RoleBehaviorTest : GuildEntityEqualityTest<RoleBehavior> by GuildEntityEqualityTest({ id, guildId ->
    RoleBehavior(guildId = guildId, id = id, kord = mockk())
})