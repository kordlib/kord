package dev.kord.core.entity

import dev.kord.common.entity.EntitlementType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.EntitlementData
import dev.kord.core.hash
import dev.kord.rest.request.RestRequestException
import kotlinx.datetime.Instant

/**
 * An instance of a [Discord Entitlement](https://discord.com/developers/docs/resources/entitlement).
 *
 * Entitlements represent that a [User] or [Guild] has access to a premium offering in your [Application].
 */
public class Entitlement(
    public val data: EntitlementData,
    override val kord: Kord,
) : KordEntity {
    override val id: Snowflake
        get() = data.id

    /**
     * The ID of the [Sku] this entitlement is for.
     */
    public val skuId: Snowflake
        get() = data.skuId

    /**
     * The ID of the [Application] this entitlement is for.
     */
    public val applicationId: Snowflake
        get() = data.applicationId

    /**
     * The ID of the [User] that is granted access to this entitlement's [Sku].
     */
    public val userId: Snowflake?
        get() = data.userId.value

    /**
     * The behavior of the [User] that is granted access to this entitlement's [Sku].
     */
    public val user: UserBehavior?
        get() = userId?.let { UserBehavior(it, kord) }

    /**
     * The [type of entitlement][EntitlementType].
     */
    public val type: EntitlementType
        get() = data.type

    /**
     * Whether this entitlement has been deleted.
     */
    public val isDeleted: Boolean
        get() = data.deleted

    /**
     * The start date at which the entitlement is valid.
     */
    public val startsAt: Instant?
        get() = data.startsAt.value

    /**
     * Date at which the entitlement is no longer valid
     */
    public val endsAt: Instant?
        get() = data.endsAt.value

    /**
     * For consumable items, whether the entitlement has been consumed.
     */
    public val isConsumed: Boolean?
        get() = data.consumed.value

    /**
     * The ID of the [Guild] that is granted access to this entitlement's [Sku].
     */
    public val guildId: Snowflake?
        get() = data.guildId.value

    /**
     * The behavior of the [Guild] that is granted access to this entitlement's [Sku].
     */
    public val guild: GuildBehavior?
        get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * Requests to delete this currently active test entitlement.
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

    /**
     * For One-Time Purchase consumable SKUs, marks a given entitlement for the user as consumed.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun consume() {
        kord.rest.entitlement.consumeEntitlement(applicationId, id)
    }

    override fun hashCode(): Int = hash(id, applicationId)

    override fun equals(other: Any?): Boolean = when (other) {
        is Entitlement -> other.id == id && other.applicationId == applicationId
        else -> false
    }

    override fun toString(): String = "Entitlement(data=$data, kord=$kord)"
}
