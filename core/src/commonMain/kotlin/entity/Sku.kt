package dev.kord.core.entity

import dev.kord.common.entity.DiscordSku
import dev.kord.common.entity.SkuFlags
import dev.kord.common.entity.SkuType
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow

/**
 * An instance of a [Discord Sku](https://discord.com/developers/docs/monetization/skus).
 *
 * SKUs (or stock-keeping units) represent premium offerings that can be made available to your [Application]'s [User]s or
 * [Guild]s.
 */
public class Sku(
    public val data: DiscordSku,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordEntity, Strategizable {
    override val id: Snowflake
        get() = data.id

    /**
     * The type of this SKU.
     */
    public val type: SkuType get() = data.type

    /**
     * The ID of the [Application] this SKU is for.
     */
    public val applicationId: Snowflake get() = data.applicationId

    /**
     * Customer-facing name of the premium offering.
     */
    public val name: String get() = data.name

    /**
     * System-generated URL slug based on the SKU's name
     */
    public val slug: String get() = data.slug

    /**
     * The flags of this SKU.
     */
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

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Sku = Sku(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = hash(id, applicationId)

    override fun equals(other: Any?): Boolean = when (other)  {
        is Sku -> other.id == id && other.applicationId == applicationId
        else -> false
    }

    override fun toString(): String {
        return "Sku(data=$data, kord=$kord, supplier=$supplier)"
    }
}