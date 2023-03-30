package dev.kord.core.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.cache.data.RoleData
import dev.kord.core.equality.BehaviorEqualityTest
import dev.kord.core.equality.GuildEntityEqualityTest
import dev.kord.core.mockKord
import io.mockk.every
import io.mockk.mockk

internal class RoleTest : GuildEntityEqualityTest<Role> by GuildEntityEqualityTest({ id, guildId ->
    val kord = mockKord()
    val data = mockk<RoleData>()
    every { data.id } returns id
    every { data.guildId } returns guildId
    every { data.icon } returns Optional.Missing()
    every { data.unicodeEmoji } returns Optional.Missing()
    Role(data, kord)
}), BehaviorEqualityTest<Role> {
    override fun Role.behavior(): KordEntity = RoleBehavior(guildId = guildId, id = id, kord = kord)
}
