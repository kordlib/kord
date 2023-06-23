package dev.kord.core.behavior

import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord

internal class WebhookBehaviorTest : EntityEqualityTest<WebhookBehavior> by EntityEqualityTest({
    val kord = mockKord()
    WebhookBehavior(it, kord)
})
