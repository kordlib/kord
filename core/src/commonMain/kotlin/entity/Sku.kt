package dev.kord.core.entity

import dev.kord.common.entity.SkuFlags
import dev.kord.common.entity.SkuType
import dev.kord.common.exception.RequestException
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.SkuData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow

public class Sku(
    public val data: SkuData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordEntity, Strategizable {
    override val id: Snowflake
        get() = data.id

    public val applicationId: Snowflake get() = data.applicationId

    public val type: SkuType get() = data.type

    /**
     * Customer-facing name of the premium offering.
     */
    public val name: String get() = data.name

    /**
     * System-generated URL slug based on the SKU's name
     */
    public val slug: String get() = data.slug

    public val flags: SkuFlags get() = data.flags

    /**
     * Requests to get the entitlements for this SKU.
     *
     * @param limit The maximum number of entitlements to return. Default is 100.
     * @param userId The ID of the user to get entitlements for. If not provided, the current user is assumed.
     * @param guildId The ID of the guild to get entitlements for. If not provided, entitlements for all guilds are returned.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getEntitlements(
        limit: Int? = null,
        userId: Snowflake? = null,
        guildId: Snowflake? = null,
    ): Flow<Entitlement> = supplier.getEntitlements(applicationId, id, limit, userId, guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable = Sku(data, kord, strategy.supply(kord))
}