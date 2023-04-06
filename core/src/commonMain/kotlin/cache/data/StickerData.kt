package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class StickerData(
    val id: Snowflake,
    val packId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String,
    val description: String?,
    val tags: Optional<String> = Optional.Missing(),
    val formatType: MessageStickerType,
    val available: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val user: Optional<UserData> = Optional.Missing(),
    @SerialName("sort_value")
    val sortValue: OptionalInt = OptionalInt.Missing
) {
    public companion object {

        public val description: DataDescription<StickerData, Snowflake> = description(StickerData::id) {
            link(StickerData::guildId to GuildData::id)
            link(StickerData::packId to StickerPackData::id)
        }


        public fun from(entity: DiscordMessageSticker): StickerData = with(entity) {
            StickerData(id, packId, name, description, tags, formatType, available, guildId, user.map { it.toData() }, sortValue)
        }
    }
}

@Serializable
public data class StickerItemData(
    val id: Snowflake,
    val name: String,
    val formatType: MessageStickerType
) {
    public companion object {
        public fun from(entity: DiscordStickerItem): StickerItemData = with(entity) {
            StickerItemData(id, name, formatType)
        }
    }
}


public data class StickerPackData(
    val id: Snowflake,
    val stickers: List<StickerData>,
    val name: String,
    val skuId: Snowflake,
    val coverStickerId: OptionalSnowflake = OptionalSnowflake.Missing,
    val description: String,
    val bannerAssetId: Snowflake
    ) {
    public companion object {

        public val description: DataDescription<StickerPackData, Snowflake> = description(StickerPackData::id)

        public fun from(entity: DiscordStickerPack): StickerPackData = with(entity) {
            StickerPackData(id, stickers.map { StickerData.from(it) }, name, skuId, coverStickerId, description, bannerAssetId)
        }
    }
}