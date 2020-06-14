package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import equality.EntityEqualityTest
import mockKord

internal class MessageBehaviorTest : EntityEqualityTest<MessageBehavior> by EntityEqualityTest({
    val kord = mockKord()
    MessageBehavior(it, Snowflake(0), kord)
})