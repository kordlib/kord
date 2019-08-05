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

}

suspend inline fun WebhookBehavior.embed(builder: UpdateWebhookBuilder.() -> Unit) : Nothing /*Webhook*/ = TODO()