package dev.kord.core.entity

import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.UserData
import dev.kord.core.equality.BehaviorEqualityTest
import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord
import io.mockk.every
import io.mockk.mockk

internal class UserTest : EntityEqualityTest<User> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<UserData>()
    every { data.id } returns it
    User(data, kord)
}), BehaviorEqualityTest<User> {
    override fun User.behavior(): KordEntity = UserBehavior(id = id, kord = kord)
}
