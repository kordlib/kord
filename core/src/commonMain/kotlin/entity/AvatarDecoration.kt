package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.AvatarDecorationData

public class AvatarDecoration(
    public val data: AvatarDecorationData,
    override val kord: Kord
) : KordEntity {
    override val id: Snowflake get() = data.skuId

    public val assetHash: String get() = data.asset

    public val asset: Asset get() = Asset.avatarDecoration(assetHash, kord)
}