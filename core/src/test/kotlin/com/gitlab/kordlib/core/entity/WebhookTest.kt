package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.WebhookBehavior
import com.gitlab.kordlib.core.cache.data.WebhookData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import mockKord

internal class WebhookTest : EntityEqualityTest<Webhook> by EntityEqualityTest({
    val kord = mockKord()
    val data = mockk<WebhookData>()
    every { data.id } returns it
    Webhook(data, kord)
}), BehaviorEqualityTest<Webhook> {
    override fun Webhook.behavior(): Entity = WebhookBehavior(id = id, kord = kord)
}