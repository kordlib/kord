package dev.kord.core

import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.WebhookBehavior
import dev.kord.core.entity.Webhook
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.request.RestRequestException

/**
 * Client for interacting with webhooks.
 */
public interface WebhookClient {
    public val resources: ClientResources

    /**
     * A reference to [unsafe][WebhookUnsafe] [Webhook] constructors.
     */
    @KordUnsafe
    public val unsafe: WebhookUnsafe

    /**
     * Requests to get the [Webhook] in this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the webhook was not present.
     */
    public suspend fun getWebhook(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook

    /**
     * Requests to get the [Webhook] in this guild with an authentication token,
     * returns null if the webhook was not present.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */

    public suspend fun getWebhookOrNull(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook?

    /**
     * Requests to get the [Webhook] in this guild with an authentication token.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the webhook was not present.
     */
    public suspend fun getWebhookWithToken(
        id: Snowflake,
        token: String,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook

    /**
     * Requests to get the [Webhook] in this guild with an authentication token,
     * returns null if the webhook was not present.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun getWebhookWithTokenOrNull(
        id: Snowflake,
        token: String,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook?
}

/**
 * Unsafe constructor for [WebhookBehaviors][WebhookBehavior].
 *
 * Using these won't check whether the underlying entity actually exists and might
 * throw an [RestRequestException] if the id is invalid.
 */
@KordUnsafe
public interface WebhookUnsafe {
    /**
     * Constructs a new [WebhookBehavior] without checking whether the underlying
     * Webhook exists.
     */
    public fun webhook(id: Snowflake): WebhookBehavior
}
