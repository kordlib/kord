package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord

internal class MessageBehaviorTest : EntityEqualityTest<MessageBehavior> by EntityEqualityTest({
    val kord = mockKord()
    MessageBehavior(it, Snowflake(0u), kord)
})
