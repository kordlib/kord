package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.cache.data.ApplicationInfoData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk

internal class ApplicationInfoTest : EntityEqualityTest<ApplicationInfo> by EntityEqualityTest({
    val data = mockk<ApplicationInfoData>()
    every { data.id } returns it.longValue
    ApplicationInfo(data, mockk())
})