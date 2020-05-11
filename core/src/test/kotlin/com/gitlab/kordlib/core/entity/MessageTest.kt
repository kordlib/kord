package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.cache.data.MessageData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk

internal class MessageTest : EntityEqualityTest<Message> by EntityEqualityTest({
    val data = mockk<MessageData>()
    every { data.id } returns it.longValue
    every { data.channelId } returns it.longValue
    Message(data, mockk())
}), BehaviorEqualityTest<Message> {
    override fun Message.behavior(): Entity = MessageBehavior(messageId = id, channelId = id, kord = kord)
}