package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordGuildPreview
import dev.kord.common.entity.GuildFeature
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public class GuildPreviewData(
    public val id: Snowflake,
    public val name: String,
    public val icon: String? = null,
    public val splash: String? = null,
    public val discoverySplash: String? = null,
    public val emojis: List<EmojiData>,
    public val features: List<GuildFeature>,
    public val approximateMemberCount: Int,
    public val approximatePresenceCount: Int,
    public val description: String? = null,
    public val stickers: List<StickerData>,
) {
    public companion object {
        public fun from(entity: DiscordGuildPreview): GuildPreviewData = with(entity) {
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
                description,
                stickers.map { StickerData.from(it) },
            )
        }
    }
}
