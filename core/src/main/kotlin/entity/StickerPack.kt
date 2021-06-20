package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.StickerPackData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

class StickerPack(
    val data: StickerPackData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity, Strategizable {
    override val id: Snowflake
        get() = data.id
    val stickers: List<Sticker>
        get() = data.stickers.map { Sticker.from(it, kord, supplier) }
    val name: String
        get() = data.name
    val skuId: Snowflake
        get() = data.skuId
    val coverStickerId: Snowflake
        get() = data.coverStickerId
    val description: String
        get() = data.description
    val bannerAssetId: String
        get() = data.bannerAssetId

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable = StickerPack(
        data, kord, strategy.supply(kord)
    )

    override fun toString(): String {
        return "StickerPack(data=$data, kord=$kord, supplier=$supplier)"
    }
}
