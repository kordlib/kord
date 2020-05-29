package com.gitlab.kordlib.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordGuildPreview(
        /**
         * Guild id.
         */
        val id: String,
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
        @SerialName("discovery_splash")
        val discoverySplash: String? = null,

        /**
         * Custom guild emojis.
         */
        val emojis: List<DiscordEmoji>,

        /**
         * Enabled guild features.
         */
        val features: List<GuildFeature>,

        /**
         * Approximate number of members in this guild.
         */
        @SerialName("approximate_member_count")
        val approximateMemberCount: Int,

        /**
         * Approximate number of online members in this guild.
         */
        @SerialName("approximate_presence_count")
        val approximatePresenceCount: Int,

        /**
         * The description for the guild.
         */
        val description: String? = null
)
