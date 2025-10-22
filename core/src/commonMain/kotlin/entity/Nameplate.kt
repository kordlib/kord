package dev.kord.core.entity

import dev.kord.common.entity.NameplatePalette
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.NameplateData

public class Nameplate(
    public val data: NameplateData,
    override val kord: Kord
) : KordEntity {
    override val id: Snowflake get() = data.skuId

    public val assetHash: String get() = data.asset

    public val asset: Asset get() = Asset.nameplate(assetHash, kord)

    public val label: String get() = data.label

    public val palette: NameplatePalette get() = data.palette
}