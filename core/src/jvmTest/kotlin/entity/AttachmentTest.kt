package dev.kord.core.entity

import dev.kord.core.cache.data.AttachmentData
import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord
import io.mockk.every
import io.mockk.mockk

internal class AttachmentTest : EntityEqualityTest<Attachment> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<AttachmentData>()
    every { data.id } returns it
    Attachment(data, kord)
})
