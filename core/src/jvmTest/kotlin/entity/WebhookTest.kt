package dev.kord.core.entity

import dev.kord.core.behavior.WebhookBehavior
import dev.kord.core.cache.data.WebhookData
import dev.kord.core.equality.BehaviorEqualityTest
import dev.kord.core.equality.EntityEqualityTest
import dev.kord.core.mockKord
import io.mockk.every
import io.mockk.mockk

internal class WebhookTest : EntityEqualityTest<Webhook> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<WebhookData>()
    every { data.id } returns it
    Webhook(data, kord)
}), BehaviorEqualityTest<Webhook> {
    override fun Webhook.behavior(): KordEntity = WebhookBehavior(id = id, kord = kord)
}
