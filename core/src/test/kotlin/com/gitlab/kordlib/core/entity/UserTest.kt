package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.cache.data.UserData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class UserTest : EntityEqualityTest<User> by EntityEqualityTest({
    val data = mockk<UserData>()
    every { data.id } returns it.longValue
    User(data, mockk())
}), BehaviorEqualityTest<User> {
    override fun User.behavior(): Entity = UserBehavior(id = id, kord = kord)
}