package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.cache.data.MessageData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class MessageTest : EntityEqualityTest<Message> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<MessageData>()
    every { data.id } returns it.longValue
    every { data.channelId } returns it.longValue
    Message(data, kord)
}), BehaviorEqualityTest<Message> {
    override fun Message.behavior(): Entity = MessageBehavior(messageId = id, channelId = id, kord = kord)
}