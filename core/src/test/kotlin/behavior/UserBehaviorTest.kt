package dev.kord.core.behavior

import equality.EntityEqualityTest
import mockKord

internal class UserBehaviorTest : EntityEqualityTest<UserBehavior> by EntityEqualityTest({
    val kord = mockKord()
    UserBehavior(it, kord)
})