package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.StickerData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Sticker
import dev.kord.core.entity.Strategizable
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.guild.StickerModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface StickerBehavior : KordEntity, Strategizable {

    public val guildId: Snowflake

    public suspend fun delete() {
        return kord.rest.sticker.deleteSticker(guildId, id)
    }

    public suspend fun asSticker(): Sticker = supplier.getSticker(id)

    public suspend fun asStickerOrNull(): Sticker? = supplier.getStickerOrNull(id)

    public suspend fun fetchSticker(): Sticker = supplier.getSticker(id)

    public suspend fun fetchStickerOrNull(): Sticker? = supplier.getStickerOrNull(id)

}

public fun StickerBehavior(guildId: Snowflake, id: Snowflake, kord: Kord, supplier: EntitySupplier): StickerBehavior =
    object : StickerBehavior {
        override val guildId: Snowflake
            get() = guildId
        override val id: Snowflake
            get() = id
        override val kord: Kord
            get() = kord

        override val supplier: EntitySupplier
            get() = supplier

        override fun withStrategy(strategy: EntitySupplyStrategy<*>): StickerBehavior =
            StickerBehavior(guildId, id, kord, strategy.supply(kord))

    }

@OptIn(ExperimentalContracts::class)
public suspend inline fun StickerBehavior.edit(builder: StickerModifyBuilder.() -> Unit): Sticker {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.sticker.modifyGuildSticker(guildId, id, builder)
    val data = StickerData.from(response)
    return Sticker(data, kord)
}