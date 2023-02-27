package entity

import dev.kord.core.cache.data.ApplicationData
import dev.kord.core.entity.Application
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class ApplicationTest : EntityEqualityTest<Application> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<ApplicationData>()
    every { data.id } returns it
    Application(data, kord)
})
