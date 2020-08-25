package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.core.cache.data.IntegrationData
import equality.GuildEntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

@KordUnstableApi
internal class IntegrationTest : GuildEntityEqualityTest<Integration> by GuildEntityEqualityTest ({ id, guildId ->
    val kord = mockKord()
    val data = mockk<IntegrationData>()
    every { data.id } returns id.longValue
    every { data.guildId } returns guildId.longValue
    Integration(data, kord)
})