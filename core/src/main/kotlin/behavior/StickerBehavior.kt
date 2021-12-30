package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.StickerData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Sticker
import dev.kord.rest.builder.guild.StickerModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface StickerBehavior : KordEntity {

    public val guildId: Snowflake

    public suspend fun  delete() {
        return kord.rest.sticker.deleteSticker(guildId, id)
    }
}

@OptIn(ExperimentalContracts::class)
public suspend inline fun StickerBehavior.edit(builder: StickerModifyBuilder.() -> Unit): Sticker {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.sticker.modifyGuildSticker(guildId, id, builder)
    val data = StickerData.from(response)
    return Sticker(data, kord)
}