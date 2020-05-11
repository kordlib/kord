package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.cache.data.GuildData
import com.gitlab.kordlib.core.cache.data.UserData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class GuildTest: EntityEqualityTest<Guild> by EntityEqualityTest({
    val data = mockk<GuildData>()
    every { data.id } returns it.longValue
    Guild(data, mockk())
}), BehaviorEqualityTest<Guild> {
    override fun Guild.behavior(): Entity = GuildBehavior(id, kord)
}