package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.cache.data.ApplicationInfoData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class ApplicationInfoTest : EntityEqualityTest<ApplicationInfo> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<ApplicationInfoData>()
    every { data.id } returns it.longValue
    ApplicationInfo(data, kord)
})