package dev.kord.core.entity

import dev.kord.common.entity.EntitlementType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.EntitlementBehavior
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.EntitlementData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class Entitlement(
    public val data: EntitlementData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : EntitlementBehavior {
    override val applicationId: Snowflake
        get() = data.applicationId

    override val id: Snowflake
        get() = data.id

    /**
     * The type of entitlement.
     */
    public val type: EntitlementType
        get() = data.type

    /**
     * The ID of the sku this entitlement is for.
     */
    public val skuId: Snowflake
        get() = data.skuId

    /**
     * The ID of the guild this entitlement's sku is granted to.
     */
    public val guildId: Snowflake?
        get() = data.guildId.value

    public val guild: GuildBehavior?
        get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * The ID of the user this entitlement's sku is granted to.
     */
    public val userId: Snowflake?
        get() = data.userId.value

    public val user: UserBehavior?
        get() = userId?.let { UserBehavior(it, kord) }

    override suspend fun asEntitlement(): Entitlement = this

    override suspend fun asEntitlementOrNull(): Entitlement = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Entitlement =
        Entitlement(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "Entitlement(data=$data, kord=$kord, supplier=$supplier)"
    }
}
