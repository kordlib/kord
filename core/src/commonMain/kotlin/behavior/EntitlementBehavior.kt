package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.entity.Entitlement
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.rest.request.RestRequestException

/**
 * The behavior of a [Discord Test Entitlement](https://discord.com/developers/docs/monetization/entitlements)
 */
public interface EntitlementBehavior : KordEntity, Strategizable {
    public val applicationId: Snowflake

    /**
     * Requests to get this value as an [Entitlement].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the entitlement wasn't present.
     */
    public suspend fun asEntitlement(): Entitlement = supplier.getEntitlement(applicationId, id)

    /**
     * Requests to get this value as an [Entitlement].
     * returns null if this entitlement isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun asEntitlementOrNull(): Entitlement? = supplier.getEntitlementOrNull(applicationId, id)

    /**
     * Retrieve the [Entitlement] associated with this behavior from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the entitlement wasn't present.
     */
    public suspend fun fetchEntitlement(): Entitlement = supplier.getEntitlement(applicationId, id)

    /**
     * Retrieve the [Entitlement] associated with this behavior from the provided [EntitySupplier]
     * returns null if this entitlement isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchEntitlementOrNull(): Entitlement? = supplier.getEntitlementOrNull(applicationId, id)

    /**
     * Requests to delete this currently-active [test entitlement][Entitlement.isTest].
     *
     * Discord will act as though that [user][Entitlement.user] or [guild][Entitlement.guild] *no longer* has
     * entitlement to your premium offering.
     *
     * This request will fail if this is not a test entitlement.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete() {
        kord.rest.entitlement.deleteTestEntitlement(applicationId, id)
    }

}
