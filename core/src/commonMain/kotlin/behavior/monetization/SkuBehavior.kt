package dev.kord.core.behavior.monetization

import dev.kord.common.entity.EntitlementOwnerType
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.EntitlementData
import dev.kord.core.entity.Application
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.monetization.Entitlement
import dev.kord.core.entity.monetization.Sku
import dev.kord.core.entity.monetization.Subscription
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.monetization.SkuSubscriptionsListRequestBuilder
import dev.kord.rest.json.request.TestEntitlementCreateRequest
import dev.kord.rest.request.RestRequestException
import kotlinx.coroutines.flow.Flow
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

/** The behavior of an [Sku]. */
public interface SkuBehavior : KordEntity, Strategizable {

    /** The ID of the [Application] this SKU is for. */
    public val applicationId: Snowflake

    /**
     * Requests a [Subscription] containing this SKU by its [id][subscriptionId]. Returns `null` if it wasn't found.
     *
     * @throws RequestException if something went wrong during the request.
     */
    public suspend fun getSubscriptionOrNull(subscriptionId: Snowflake): Subscription? =
        supplier.getSubscriptionOrNull(skuId = this.id, subscriptionId)

    /**
     * Requests a [Subscription] containing this SKU by its [id][subscriptionId].
     *
     * @throws RequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the [Subscription] wasn't found.
     */
    public suspend fun getSubscription(subscriptionId: Snowflake): Subscription =
        supplier.getSubscription(skuId = this.id, subscriptionId)

    /**
     * Requests to create a new [test entitlement][Entitlement] to this SKU for an owner with the given [ownerId] and
     * [ownerType]. Discord will act as though that owner has entitlement to your premium offering.
     *
     * The returned [Entitlement] will not contain [startsAt][Entitlement.startsAt] and [endsAt][Entitlement.endsAt], as
     * it's valid in perpetuity.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun createTestEntitlement(ownerId: Snowflake, ownerType: EntitlementOwnerType): Entitlement {
        val response = kord.rest.entitlement.createTestEntitlement(
            applicationId,
            TestEntitlementCreateRequest(skuId = this.id, ownerId, ownerType),
        )
        return Entitlement(EntitlementData.from(response), kord)
    }

    /**
     * Requests to create a new [test entitlement][Entitlement] to this SKU for a given [user]. Discord will act as
     * though that user has entitlement to your premium offering.
     *
     * The returned [Entitlement] will not contain [startsAt][Entitlement.startsAt] and [endsAt][Entitlement.endsAt], as
     * it's valid in perpetuity.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun createTestEntitlement(user: UserBehavior): Entitlement =
        createTestEntitlement(user.id, EntitlementOwnerType.User)

    /**
     * Requests to create a new [test entitlement][Entitlement] to this SKU for a given [guild]. Discord will act as
     * though that guild has entitlement to your premium offering.
     *
     * The returned [Entitlement] will not contain [startsAt][Entitlement.startsAt] and [endsAt][Entitlement.endsAt], as
     * it's valid in perpetuity.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun createTestEntitlement(guild: GuildBehavior): Entitlement =
        createTestEntitlement(guild.id, EntitlementOwnerType.Guild)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SkuBehavior
}

/**
 * Requests to get all [Subscription]s containing this [Sku].
 *
 * The returned flow is lazily executed, any [RequestException] will be thrown on
 * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
 */
public inline fun SkuBehavior.getSubscriptions(
    builder: SkuSubscriptionsListRequestBuilder.() -> Unit,
): Flow<Subscription> {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val request = SkuSubscriptionsListRequestBuilder().apply(builder).toRequest()
    return supplier.getSubscriptions(skuId = this.id, request)
}

internal class SkuBehaviorImpl(
    override val applicationId: Snowflake,
    override val id: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : SkuBehavior {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>) =
        SkuBehaviorImpl(applicationId, id, kord, strategy.supply(kord))

    override fun equals(other: Any?) =
        other is SkuBehavior && this.id == other.id && this.applicationId == other.applicationId

    override fun hashCode() = hash(id, applicationId)
    override fun toString() = "SkuBehavior(applicationId=$applicationId, id=$id, kord=$kord, supplier=$supplier)"
}
