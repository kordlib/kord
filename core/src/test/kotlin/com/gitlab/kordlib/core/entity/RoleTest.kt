package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.cache.data.RoleData
import equality.BehaviorEqualityTest
import equality.GuildEntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class RoleTest : GuildEntityEqualityTest<Role> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    val data = mockk<RoleData>()
    every { data.id } returns id.longValue
    every { data.guildId } returns guildId.longValue
    Role(data, kord)
}), BehaviorEqualityTest<Role> {
    override fun Role.behavior(): Entity = RoleBehavior(guildId = guildId, id = id, kord = kord)
}