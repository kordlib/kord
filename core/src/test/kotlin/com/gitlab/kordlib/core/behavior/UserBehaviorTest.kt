package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.cache.data.UserData
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class UserBehaviorTest : EntityEqualityTest<UserBehavior> by EntityEqualityTest({
    UserBehavior(it, mockk())
})