package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.core.cache.data.RegionData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

@KordUnstableApi
internal class RegionTest : EntityEqualityTest<Region> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<RegionData>()
    every { data.id } returns it.longValue
    Region(data, kord)
})