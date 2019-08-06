package com.gitlab.kordlib.core.behavior.webhook

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.webhook.ExecuteWebhookBuilder
import com.gitlab.kordlib.core.`object`.builder.webhook.UpdateWebhookBuilder
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

/**
 * The behavior of a [Discord Webhook](https://discordapp.com/developers/docs/resources/webhook).
 */
interface WebhookBehavior : Entity {

    suspend fun delete() {
        kord.rest.webhook.deleteWebhook(id.value)
    }

    suspend fun delete(token: String) {
        kord.rest.webhook.deleteWebhookWithToken(id.value, token)
    }

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) : WebhookBehavior = object : WebhookBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

suspend inline fun WebhookBehavior.embed(builder: UpdateWebhookBuilder.() -> Unit): Nothing /*Webhook*/ = TODO()

suspend inline fun WebhookBehavior.execute(token: String, builder: ExecuteWebhookBuilder.() -> Unit) {
    val request = ExecuteWebhookBuilder().apply(builder).toRequest()
    kord.rest.webhook.executeWebhook(token = token, webhookId = id.value, request = request, wait = true)
}