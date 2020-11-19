package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordEmoji
import com.gitlab.kordlib.common.entity.DiscordGuildPreview
import com.gitlab.kordlib.common.entity.GuildFeature
import com.gitlab.kordlib.common.entity.Snowflake
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
                                emojis.map { EmojiData.from(guildId = id ,id = it.id!!, entity = it) },
                                features,
                                approximateMemberCount,
                                approximatePresenceCount,
                                description
                        )
                }
        }

}
