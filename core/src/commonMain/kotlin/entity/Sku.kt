package dev.kord.core.entity

import dev.kord.common.entity.DiscordSku
import dev.kord.common.entity.SkuFlags
import dev.kord.common.entity.SkuType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.hash

/**
 * An instance of a [Discord Sku](https://discord.com/developers/docs/resources/sku).
 *
 * SKUs (or stock-keeping units) represent premium offerings that can be made available to your [Application]'s [User]s
 * or [Guild]s.
 */
public class Sku(
    public val data: DiscordSku,
    override val kord: Kord,
) : KordEntity {
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
     * System-generated URL slug based on the SKU's name.
     */
    public val slug: String get() = data.slug

    /**
     * The flags of this SKU.
     */
    public val flags: SkuFlags get() = data.flags

    override fun hashCode(): Int = hash(id, applicationId)

    override fun equals(other: Any?): Boolean = when (other) {
        is Sku -> other.id == id && other.applicationId == applicationId
        else -> false
    }

    override fun toString(): String {
        return "Sku(data=$data, kord=$kord)"
    }
}
