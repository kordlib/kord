package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.cache.data.IntegrationData
import com.gitlab.kordlib.rest.json.response.IntegrationExpireBehavior
import equality.GuildEntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class IntegrationTest : GuildEntityEqualityTest<Integration> by GuildEntityEqualityTest ({ id, guildId ->
    val data = mockk<IntegrationData>()
    every { data.id } returns id.longValue
    every { data.guildId } returns guildId.longValue
    Integration(data, mockk())
})