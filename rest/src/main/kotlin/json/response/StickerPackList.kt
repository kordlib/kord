package dev.kord.rest.json.response

import dev.kord.common.entity.DiscordStickerPack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StickerPackList(@SerialName("sticker_packs") val stickerPacks: List<DiscordStickerPack>)
