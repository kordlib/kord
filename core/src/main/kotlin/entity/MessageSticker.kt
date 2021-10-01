package dev.kord.core.entity

import dev.kord.common.entity.MessageStickerType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.MessageStickerData

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
     * The sticker image hash asset (currently private).
     */
    public val asset: String
        get() = data.asset

    /**
     * The sticker preview image has asset (currently private).
     */
    public val previewAsset: String?
        get() = data.previewAsset.value

    /**
     * The type of sticker image.
     */
    public val formatType: MessageStickerType
        get() = data.formatType

}
