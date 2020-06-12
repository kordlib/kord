package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.cache.data.RegionData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk

internal class RegionTest : EntityEqualityTest<Region> by EntityEqualityTest({
    val data = mockk<RegionData>()
    every { data.id } returns it.longValue
    Region(data, mockk())
})