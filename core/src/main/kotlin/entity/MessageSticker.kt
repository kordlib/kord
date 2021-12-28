package dev.kord.core.entity

import dev.kord.common.entity.MessageStickerType
import dev.kord.common.entity.MessageType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.cache.data.MessageStickerData
import dev.kord.core.cache.data.StickerItemData
import dev.kord.core.cache.data.StickerPackData

/**
 * A sticker image that can be used in messages.
 */
public class MessageSticker(public val data: MessageStickerData, override val kord: Kord) : KordEntity {

    /**
     * The id of the sticker.
     */
    override val id: Snowflake
        get() = data.id

    /**
     * The id of the pack that contains this sticker.
     */
    public val packId: Snowflake
        get() = data.packId

    /**
     * The name of the sticker.
     */
    public val name: String
        get() = data.name

    /**
     * The description of the sticker.
     */
    public val description: String
        get() = data.description

    /**
     * The tags of the sticker.
     */
    public val tags: List<String>
        get() = data.tags.value?.split(",")?.map { it.trim() }.orEmpty()

    /**
     * The type of sticker image.
     */
    public val formatType: MessageStickerType
        get() = data.formatType

    public val available: Boolean
        get() = data.available.discordBoolean

    public val sortValue: Int?
        get() = data.sortValue.value

    public val guildId: Snowflake?
        get() = data.guildId.value

    public val user: User?
        get() = data.user.unwrap { User(it, kord) }
}


public class StickerItem(public val data: StickerItemData) : Entity {

    public override val id: Snowflake
        get() = data.id

    public val name: String
        get() = data.name

    public val formatType: MessageStickerType
        get() = data.formatType
}

public class StickerPack(public val data: StickerPackData, override val kord: Kord) : KordEntity {

    public override val id: Snowflake
        get() = data.id

    public val name: String get() = data.name

    public val skuId: Snowflake get() = data.skuId

    public val coverStickerId: Snowflake? get() = data.coverStickerId.value

    public val description: String get() = data.description

    public val stickers: List<MessageSticker> get() = data.stickers.map { MessageSticker(it, kord) }


}