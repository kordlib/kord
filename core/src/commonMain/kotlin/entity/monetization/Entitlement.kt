package dev.kord.core.entity.monetization

import dev.kord.common.entity.EntitlementType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.EntitlementData
import dev.kord.core.entity.Application
import dev.kord.core.entity.Guild
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.User
import dev.kord.core.hash
import dev.kord.rest.request.RestRequestException
import kotlin.time.Instant

/**
 * An instance of an [Entitlement](https://discord.com/developers/docs/resources/entitlement).
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
     * The type of this entitlement.
     */
    public val type: EntitlementType
        get() = data.type

    /**
     * Whether this entitlement has been deleted.
     */
    public val isDeleted: Boolean
        get() = data.deleted

    /**
     * The start date at which this entitlement is valid.
     *
     * Not present when using test entitlements.
     */
    public val startsAt: Instant?
        get() = data.startsAt.value

    /**
     * The date at which this entitlement is no longer valid.
     *
     * Not present when using test entitlements.
     */
    public val endsAt: Instant?
        get() = data.endsAt.value

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
     * For consumable items, whether this entitlement has been consumed.
     */
    public val isConsumed: Boolean?
        get() = data.consumed.value

    /**
     * For One-Time Purchase consumable [Sku]s, marks this entitlement for the [user] as [consumed][isConsumed].
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun consume() {
        kord.rest.entitlement.consumeEntitlement(applicationId, id)
    }

    /**
     * Requests to delete this currently active test entitlement.
     *
     * Discord will act as though that [user] or [guild] *no longer* has entitlement to your premium offering.
     *
     * This request will fail if this is not a test entitlement.
     *
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun delete() {
        kord.rest.entitlement.deleteTestEntitlement(applicationId, id)
    }

    override fun equals(other: Any?): Boolean =
        other is Entitlement && this.id == other.id && this.applicationId == other.applicationId

    override fun hashCode(): Int = hash(id, applicationId)

    override fun toString(): String = "Entitlement(data=$data, kord=$kord)"
}
