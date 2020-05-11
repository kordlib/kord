package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.cache.data.AttachmentData
import com.gitlab.kordlib.core.cache.data.EmojiData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class AttachmentTest : EntityEqualityTest<Attachment> by EntityEqualityTest({
    val data = mockk<AttachmentData>()
    every { data.id } returns it.longValue
    Attachment(data, mockk())
})
