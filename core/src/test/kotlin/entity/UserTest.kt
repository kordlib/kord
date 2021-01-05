package dev.kord.core.entity

import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.UserData
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
    override fun User.behavior(): KordEntity = UserBehavior(id = id, kord = kord)
}