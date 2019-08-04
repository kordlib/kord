package com.gitlab.kordlib.core.behavior.webhook

import com.gitlab.kordlib.core.`object`.builder.webhook.UpdateWebhookBuilder
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

interface WebhookBehavior : Entity {
    val channelId: Snowflake
    val channel: ChannelBehavior get() = ChannelBehavior(channelId, kord)

    suspend fun delete() {
        kord.rest.webhook.deleteWebhook(id.value)
    }

    suspend fun edit(builder: UpdateWebhookBuilder) : Nothing /*Webhook*/ = TODO()

}

suspend inline fun WebhookBehavior.embed(builder: UpdateWebhookBuilder.() -> Unit) : Nothing /*Webhook*/ = edit(UpdateWebhookBuilder().also(builder))