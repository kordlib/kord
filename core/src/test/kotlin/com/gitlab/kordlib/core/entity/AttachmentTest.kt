package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.core.cache.data.AttachmentData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

@KordUnstableApi
internal class AttachmentTest : EntityEqualityTest<Attachment> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<AttachmentData>()
    every { data.id } returns it.longValue
    Attachment(data, kord)
})
