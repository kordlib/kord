package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.UserData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class UserTest : EntityEqualityTest<User> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<UserData>()
    every { data.id } returns it
    User(data, kord)
}), BehaviorEqualityTest<User> {
    override fun User.behavior(): Entity = UserBehavior(id = id, kord = kord)
}