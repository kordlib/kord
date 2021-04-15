package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.DiscordGuildPreview
import dev.kord.common.entity.GuildFeature
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GuildPreviewData(
    val id: Snowflake,
    val name: String,
    val icon: String? = null,
    val splash: String? = null,
    val discoverySplash: String? = null,
    val emojis: List<EmojiData>,
    val features: List<GuildFeature>,
    val approximateMemberCount: Int,
    val approximatePresenceCount: Int,
    val description: String? = null
) {
    companion object {
        fun from(entity: DiscordGuildPreview) = with(entity) {
            GuildPreviewData(
                id,
                name,
                icon,
                splash,
                discoverySplash,
                emojis.map { EmojiData.from(guildId = id, id = it.id!!, entity = it) },
                features,
                approximateMemberCount,
                approximatePresenceCount,
                description
            )
        }
    }

}
