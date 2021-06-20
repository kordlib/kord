package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordSticker
import dev.kord.common.entity.StickerFormatType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
data class MessageStickerData(
    val id: Snowflake,
    val packId: Snowflake,
    val name: String,
    val description: String,
    val tags: Optional<String> = Optional.Missing(),
    val asset: String,
    val previewAsset: Optional<String?> = Optional.Missing(),
    val formatType: StickerFormatType,
) {

    companion object {
        fun from(entity: DiscordSticker): MessageStickerData = with(entity) {
            MessageStickerData(id, packId, name, description, tags, asset, previewAsset, formatType)
        }
    }

}
