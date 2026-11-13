package dev.kord.core.entity

import dev.kord.core.cache.data.IntegrationData
import dev.kord.core.equality.GuildEntityEqualityTest
import dev.kord.core.mockKord
import io.mockk.every
import io.mockk.mockk

internal class IntegrationTest : GuildEntityEqualityTest<Integration> by GuildEntityEqualityTest ({ id, guildId ->
    val kord = mockKord()
    val data = mockk<IntegrationData>()
    every { data.id } returns id
    every { data.guildId } returns guildId
    Integration(data, kord)
})
