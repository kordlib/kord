package dev.kord.core.entity

import dev.kord.core.cache.data.ApplicationData
import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord
import io.mockk.every
import io.mockk.mockk

internal class ApplicationTest : EntityEqualityTest<Application> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<ApplicationData>()
    every { data.id } returns it
    Application(data, kord)
})
