package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.webhook.ExecuteWebhookBuilder
import com.gitlab.kordlib.core.`object`.builder.webhook.WebhookModifyBuilder
import com.gitlab.kordlib.core.`object`.data.WebhookData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.Webhook

/**
 * The behavior of a [Discord Webhook](https://discordapp.com/developers/docs/resources/webhook).
 */
interface WebhookBehavior : Entity {

    /**
     * Requests to delete this webhook, this user must be the creator.
     */
    suspend fun delete(reason: String? = null) {
        kord.rest.webhook.deleteWebhook(id.value, reason)
    }

    /**
     * Requests to delete this webhook.
     */
    suspend fun delete(token: String, reason: String? = null) {
        kord.rest.webhook.deleteWebhookWithToken(id.value, token, reason)
    }

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) : WebhookBehavior = object : WebhookBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to edit the webhook, this user must be the creator.
 *
 * @return The updated [Webhook].
 */
suspend inline fun WebhookBehavior.edit(builder: WebhookModifyBuilder.() -> Unit): Webhook {
    val builder = WebhookModifyBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.webhook.modifyWebhook(id.value, request, reason)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}

/**
 * Requests to edit the webhook.
 *
 * @return The updated [Webhook].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun WebhookBehavior.edit(token: String, builder: WebhookModifyBuilder.() -> Unit): Webhook {
    val builder = WebhookModifyBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.webhook.modifyWebhookWithToken(id.value, token, request, reason)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}

/**
 * Requests to execute this webhook.
 */
suspend inline fun WebhookBehavior.execute(token: String, builder: ExecuteWebhookBuilder.() -> Unit) {
    val request = ExecuteWebhookBuilder().apply(builder).toRequest()
    kord.rest.webhook.executeWebhook(token = token, webhookId = id.value, request = request, wait = true)
}