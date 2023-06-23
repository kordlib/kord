package dev.kord.core.entity

import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.GuildData
import dev.kord.core.equality.BehaviorEqualityTest
import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord
import io.mockk.every
import io.mockk.mockk

internal class GuildTest: EntityEqualityTest<Guild> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<GuildData>()
    every { data.id } returns it
    Guild(data, kord)
}), BehaviorEqualityTest<Guild> {
    override fun Guild.behavior(): KordEntity = GuildBehavior(id, kord)
}
