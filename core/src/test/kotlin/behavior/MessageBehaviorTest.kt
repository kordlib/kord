package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import equality.EntityEqualityTest
import mockKord

internal class MessageBehaviorTest : EntityEqualityTest<MessageBehavior> by EntityEqualityTest({
    val kord = mockKord()
    MessageBehavior(it, Snowflake(0u), kord)
})
