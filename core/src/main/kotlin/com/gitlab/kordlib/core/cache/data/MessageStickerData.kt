package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordMessageSticker
import com.gitlab.kordlib.common.entity.MessageStickerType
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
data class MessageStickerData(
        val id: Snowflake,
        val packId: Snowflake,
        val name: String,
        val description: String,
        val tags: Optional<String>,
        val asset: String,
        val previewAsset: String?,
        val formatType: MessageStickerType,
) {

    companion object {
        fun from(entity: DiscordMessageSticker): MessageStickerData = with(entity){
            MessageStickerData(id, packId, name, description, tags, asset, previewAsset, formatType)
        }
    }

}
