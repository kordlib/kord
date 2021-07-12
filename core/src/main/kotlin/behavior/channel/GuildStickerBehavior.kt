package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.rest.builder.sticker.ModifyStickerBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface GuildStickerBehavior : KordEntity, Strategizable {
    val guildId: Snowflake

    suspend fun getGuildOrNull() = supplier.getGuildOrNull(guildId)

    suspend fun getGuild() = supplier.getGuild(guildId)

    suspend fun delete() = kord.rest.guild.deleteGuildSticker(guildId, id)

    suspend fun asGuildSticker() = supplier.getGuildSticker(guildId, id)
    suspend fun asGuildStickerOrNull() = supplier.getGuildStickerOrNull(guildId, id)

}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildStickerBehavior.modify(builder: ModifyStickerBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val request = ModifyStickerBuilder().apply(builder).toRequest()
    kord.rest.guild.modifyGuildSticker(guildId, id, request)
}
