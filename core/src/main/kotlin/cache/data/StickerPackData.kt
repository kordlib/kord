package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordStickerPack
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class StickerPackData(
    val id: Snowflake,
    val stickers: List<StickerData>,
    val name: String,
    val skuId: Snowflake,
    val coverStickerId: Snowflake,
    val description: String,
    val bannerAssetId: String
) {
    companion object {
        fun from(pack: DiscordStickerPack) =
            with(pack) {
                StickerPackData(id, stickers.map {
                    StickerData.from(it)
                }, name, skuId, coverStickerId, description, bannerAssetId)
            }
    }
}
