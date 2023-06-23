package dev.kord.core.behavior

import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord

internal class GuildBehaviorTest: EntityEqualityTest<GuildBehavior> by EntityEqualityTest({
    val kord = mockKord()
    GuildBehavior(it, kord)
})
