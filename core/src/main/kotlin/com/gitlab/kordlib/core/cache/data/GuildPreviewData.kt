package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordEmoji
import com.gitlab.kordlib.common.entity.DiscordGuildPreview
import com.gitlab.kordlib.common.entity.GuildFeature
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GuildPreviewData(
        /**
         * Guild id.
         */
        val id: Long,
        /**
         * Guild name (2-100) characters.
         */
        val name: String,

        /**
         * Icon hash.
         */
        val icon: String? = null,

        /**
         * Splash hash.
         */
        val splash: String? = null,

        /**
         * Discovery splash hash.
         */
        val discoverySplash: String? = null,

        /**
         * Ids of custom guild emojis.
         */
        val emojis: List<EmojiData>,

        /**
         * Enabled guild features.
         */
        val features: List<GuildFeature>,

        /**
         * Approximate number of members in this guild.
         */
        val approximateMemberCount: Int,

        /**
         * Approximate number of online members in this guild.
         */
        val approximatePresenceCount: Int,

        /**
         * The description for the guild.
         */
        val description: String? = null
) {
        companion object {
                fun from(entity: DiscordGuildPreview) = with(entity) {
                        GuildPreviewData(
                                id.toLong(),
                                name,
                                icon,
                                splash,
                                discoverySplash,
                                emojis.map { EmojiData.from(it.id!!, it) },
                                features,
                                approximateMemberCount,
                                approximatePresenceCount,
                                description
                        )
                }
        }

}
