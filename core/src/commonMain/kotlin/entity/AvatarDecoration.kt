package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.AvatarDecorationData

/**
 * An instance of an [Avatar Decoration](https://discord.com/developers/docs/resources/user#avatar-decoration-data-object)
 */
public class AvatarDecoration(
    public val data: AvatarDecorationData,
    override val kord: Kord
) : KordEntity {
    override val id: Snowflake get() = data.skuId

    /**
     * The hash of the avatar decoration.
     */
    public val assetHash: String get() = data.asset

    /**
     * The avatar decoration.
     */
    public val asset: Asset get() = Asset.avatarDecoration(assetHash, kord)
}