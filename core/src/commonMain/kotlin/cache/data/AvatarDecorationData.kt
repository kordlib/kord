package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordAvatarDecoration
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class AvatarDecorationData(
    val asset: String,
    val skuId: Snowflake
) {
    public companion object {
        public fun from(entity: DiscordAvatarDecoration): AvatarDecorationData = with(entity) {
            AvatarDecorationData(asset, skuId)
        }
    }
}

public fun DiscordAvatarDecoration.toData(): AvatarDecorationData = AvatarDecorationData.from(this)