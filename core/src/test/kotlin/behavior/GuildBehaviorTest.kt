package dev.kord.core.behavior

import equality.EntityEqualityTest
import mockKord

internal class GuildBehaviorTest: EntityEqualityTest<GuildBehavior> by EntityEqualityTest({
    val kord = mockKord()
    GuildBehavior(it, kord)
})