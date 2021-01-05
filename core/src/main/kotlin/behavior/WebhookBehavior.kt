package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.WebhookData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.Webhook
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.webhook.ExecuteWebhookBuilder
import dev.kord.rest.builder.webhook.WebhookModifyBuilder
import dev.kord.rest.request.RestRequestException
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Webhook](https://discord.com/developers/docs/resources/webhook).
 */
interface WebhookBehavior : KordEntity, Strategizable {

    /**
     * Requests to delete this webhook, this user must be the creator.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete(reason: String? = null) {
        kord.rest.webhook.deleteWebhook(id, reason)
    }

    /**
     * Requests to delete this webhook.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete(token: String, reason: String? = null) {
        kord.rest.webhook.deleteWebhookWithToken(id, token, reason)
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
                strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
        ): WebhookBehavior = object : WebhookBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)

            override fun hashCode(): Int = Objects.hash(id)

            override fun equals(other: Any?): Boolean = when (other) {
                is WebhookBehavior -> other.id == id
                else -> false
            }

            override fun toString(): String {
                return "WebhookBehavior(id=$id, kord=$kord, supplier=$supplier)"
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
@OptIn(ExperimentalContracts::class)
suspend inline fun WebhookBehavior.edit(builder: WebhookModifyBuilder.() -> Unit): Webhook {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.webhook.modifyWebhook(id, builder)
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
@OptIn(ExperimentalContracts::class)
suspend inline fun WebhookBehavior.edit(token: String, builder: WebhookModifyBuilder.() -> Unit): Webhook {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.webhook.modifyWebhookWithToken(id, token, builder)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}

/**
 * Requests to execute this webhook.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun WebhookBehavior.execute(token: String, builder: ExecuteWebhookBuilder.() -> Unit): Message {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.webhook.executeWebhook(
            token = token,
            webhookId = id,
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
@OptIn(ExperimentalContracts::class)
suspend inline fun WebhookBehavior.executeIgnored(token: String, builder: ExecuteWebhookBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.webhook.executeWebhook(token = token, webhookId = id, wait = false, builder = builder)
}

