package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.cache.data.RoleData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import equality.GuildEntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class RoleTest : GuildEntityEqualityTest<Role> by GuildEntityEqualityTest({ id, guildId ->
    val data = mockk<RoleData>()
    every { data.id } returns id.longValue
    every { data.guildId } returns guildId.longValue
    Role(data, mockk())
}), BehaviorEqualityTest<Role> {
    override fun Role.behavior(): Entity = RoleBehavior(guildId = guildId, id = id, kord = kord)
}