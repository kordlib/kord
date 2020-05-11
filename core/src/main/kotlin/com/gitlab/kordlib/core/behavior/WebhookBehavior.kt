package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.WebhookData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Webhook
import com.gitlab.kordlib.rest.builder.webhook.ExecuteWebhookBuilder
import com.gitlab.kordlib.rest.builder.webhook.WebhookModifyBuilder
import java.util.*

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
        internal operator fun invoke(id: Snowflake, kord: Kord): WebhookBehavior = object : WebhookBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord

            override fun hashCode(): Int = Objects.hash(id)

            override fun equals(other: Any?): Boolean = when(other) {
                is WebhookBehavior -> other.id == id
                else -> false
            }
        }
    }

}

/**
 * Requests to edit the webhook, this user must be the creator.
 *
 * @return The updated [Webhook].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun WebhookBehavior.edit(builder: WebhookModifyBuilder.() -> Unit): Webhook {
    val response = kord.rest.webhook.modifyWebhook(id.value, builder)
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
    val response = kord.rest.webhook.modifyWebhookWithToken(id.value, token, builder)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}

/**
 * Requests to execute this webhook.
 */
suspend inline fun WebhookBehavior.execute(token: String, builder: ExecuteWebhookBuilder.() -> Unit) {
    kord.rest.webhook.executeWebhook(token = token, webhookId = id.value, wait = true, builder = builder)
}