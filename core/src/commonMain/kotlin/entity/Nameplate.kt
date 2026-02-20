package dev.kord.core.entity

import dev.kord.common.entity.NameplatePalette
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.NameplateData

/**
 * An instance of a [nameplate](https://discord.com/developers/docs/resources/user#nameplate)
 */
public class Nameplate(
    public val data: NameplateData,
    override val kord: Kord
) : KordEntity {
    override val id: Snowflake get() = data.skuId

    /**
     * The path to the nameplate asset.
     */
    public val assetHash: String get() = data.asset

    /**
     * The nameplate asset
     */
    public val asset: Asset get() = Asset.nameplate(assetHash, kord)

    /**
     * The label for the nameplate (Currently unsued)
     */
    public val label: String get() = data.label

    /**
     * The background color of the nameplate
     *
     * @see NameplatePalette
     */
    public val palette: NameplatePalette get() = data.palette
}