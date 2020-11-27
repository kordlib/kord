package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.MessageStickerType
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.MessageStickerData

/**
 * A sticker image that can be used in messages.
 */
class MessageSticker(val data: MessageStickerData, override val kord: Kord) : Entity {

    /**
     * The id of the sticker.
     */
    override val id: Snowflake
        get() = data.id

    /**
     * The id of the pack that contains this sticker.
     */
    val packId: Snowflake
        get() = data.packId

    /**
     * The name of the sticker.
     */
    val name: String
        get() = data.name

    /**
     * The description of the sticker.
     */
    val description: String
        get() = data.description

    /**
     * The tags of the sticker.
     */
    val tags: List<String>
        get() = data.tags.value?.split(",")?.map { it.trim() }.orEmpty()

    /**
     * The sticker image hash asset (currently private).
     */
    val asset: String
        get() = data.asset

    /**
     * The sticker preview image has asset (currently private).
     */
    val previewAsset: String?
        get() = data.previewAsset

    /**
     * The type of sticker image.
     */
    val formatType: MessageStickerType
        get() = data.formatType

}
