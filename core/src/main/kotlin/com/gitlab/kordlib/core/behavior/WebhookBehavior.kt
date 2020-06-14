package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.cache.data.WebhookData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.Webhook
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.builder.webhook.ExecuteWebhookBuilder
import com.gitlab.kordlib.rest.builder.webhook.WebhookModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import java.util.*

/**
 * The behavior of a [Discord Webhook](https://discordapp.com/developers/docs/resources/webhook).
 */
interface WebhookBehavior : Entity, Strategizable {

    /**
     * Requests to delete this webhook, this user must be the creator.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete(reason: String? = null) {
        kord.rest.webhook.deleteWebhook(id.value, reason)
    }

    /**
     * Requests to delete this webhook.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete(token: String, reason: String? = null) {
        kord.rest.webhook.deleteWebhookWithToken(id.value, token, reason)
    }

    /**
     * Returns a new [WebhookBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): WebhookBehavior =
            WebhookBehavior(id, kord, strategy)

    companion object {
        internal operator fun invoke(
                id: Snowflake,
                kord: Kord,
                strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): WebhookBehavior = object : WebhookBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)

            override fun hashCode(): Int = Objects.hash(id)

            override fun equals(other: Any?): Boolean = when (other) {
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
 *
 * @throws [RestRequestException] if something went wrong during the request.
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
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Suppress("NAME_SHADOWING")
suspend inline fun WebhookBehavior.edit(token: String, builder: WebhookModifyBuilder.() -> Unit): Webhook {
    val response = kord.rest.webhook.modifyWebhookWithToken(id.value, token, builder)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}

/**
 * Requests to execute this webhook.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun WebhookBehavior.execute(token: String, builder: ExecuteWebhookBuilder.() -> Unit): Message {
    val response = kord.rest.webhook.executeWebhook(
            token = token,
            webhookId = id.value,
            wait = true,
            builder = builder
    )!!

    val data = MessageData.from(response)

    return Message(data, kord)
}

/**
 * Requests to execute this webhook.
 *
 * This is a 'fire and forget' variant of [execute]. It will not wait for a response and might not throw an
 * Exception if the request wasn't executed.
 */
suspend inline fun WebhookBehavior.executeIgnored(token: String, builder: ExecuteWebhookBuilder.() -> Unit) {
    kord.rest.webhook.executeWebhook(token = token, webhookId = id.value, wait = false, builder = builder)
}

