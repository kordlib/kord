package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.WebhookBehavior
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.cache.data.WebhookData
import equality.BehaviorEqualityTest
import equality.EntityEqualityTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class WebhookTest : EntityEqualityTest<Webhook> by EntityEqualityTest({
    val data = mockk<WebhookData>()
    every { data.id } returns it.longValue
    Webhook(data, mockk())
}), BehaviorEqualityTest<Webhook> {
    override fun Webhook.behavior(): Entity = WebhookBehavior(id = id, kord = kord)
}