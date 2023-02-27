package dev.kord.core.entity

import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.GuildData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class GuildTest: EntityEqualityTest<Guild> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<GuildData>()
    every { data.id } returns it
    Guild(data, kord)
}), BehaviorEqualityTest<Guild> {
    override fun Guild.behavior(): KordEntity = GuildBehavior(id, kord)
}