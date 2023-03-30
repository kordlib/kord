package dev.kord.core.behavior

import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord

internal class UserBehaviorTest : EntityEqualityTest<UserBehavior> by EntityEqualityTest({
    val kord = mockKord()
    UserBehavior(it, kord)
})
