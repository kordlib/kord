package dev.kord.core.entity

import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.cache.data.RoleData
import equality.BehaviorEqualityTest
import equality.GuildEntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class RoleTest : GuildEntityEqualityTest<Role> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    val data = mockk<RoleData>()
    every { data.id } returns id
    every { data.guildId } returns guildId
    Role(data, kord)
}), BehaviorEqualityTest<Role> {
    override fun Role.behavior(): KordEntity = RoleBehavior(guildId = guildId, id = id, kord = kord)
}