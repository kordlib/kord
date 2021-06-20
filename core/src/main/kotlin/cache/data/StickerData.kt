package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordSticker
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.StickerFormatType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StickerType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable

@Serializable
data class StickerData(
    val id: Snowflake,
    val packId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String,
    val description: String,
    val tags: String,
    val asset: String,
    val type: StickerType,
    val formatType: StickerFormatType,
    val available: OptionalBoolean = OptionalBoolean.Missing,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val user: Optional<DiscordUser> = Optional.Missing(),
    val sortValue: OptionalInt = OptionalInt.Missing
) {

    companion object {
        fun from(entity: DiscordSticker): StickerData = with(entity) {
            StickerData(
                id,
                packId,
                name,
                description,
                tags,
                asset,
                type,
                formatType,
                available,
                guildId,
                user,
                sortValue
            )
        }
    }

}
