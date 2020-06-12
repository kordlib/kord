package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.cache.data.MessageData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class MessageBehaviorTest : EntityEqualityTest<MessageBehavior> by EntityEqualityTest({
    MessageBehavior(it, Snowflake(0), mockk())
})