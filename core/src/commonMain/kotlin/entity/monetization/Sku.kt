package dev.kord.core.entity.monetization

import dev.kord.common.entity.DiscordSku
import dev.kord.common.entity.SkuFlags
import dev.kord.common.entity.SkuType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.monetization.SkuBehavior
import dev.kord.core.entity.Application
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of an [SKU](https://discord.com/developers/docs/resources/sku).
 *
 * SKUs (or stock-keeping units) represent premium offerings that can be made available to your [Application]'s [User]s
 * or [Guild]s.
 */
public class Sku(
    public val data: DiscordSku,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : SkuBehavior {
    override val id: Snowflake
        get() = data.id

    /**
     * The type of this SKU.
     */
    public val type: SkuType get() = data.type

    override val applicationId: Snowflake get() = data.applicationId

    /**
     * The customer-facing name of this premium offering.
     */
    public val name: String get() = data.name

    /**
     * A system-generated URL slug based on this SKU's name.
     */
    public val slug: String get() = data.slug

    /**
     * The flags of this SKU.
     */
    public val flags: SkuFlags get() = data.flags

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Sku = Sku(data, kord, strategy.supply(kord))

    override fun equals(other: Any?): Boolean =
        other is SkuBehavior && this.id == other.id && this.applicationId == other.applicationId

    override fun hashCode(): Int = hash(id, applicationId)

    override fun toString(): String = "Sku(data=$data, kord=$kord)"
}
