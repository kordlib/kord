package dev.kord.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordGuildPreview(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val splash: String?,
    @SerialName("discovery_splash")
    val discoverySplash: String?,
    val emojis: List<DiscordEmoji>,
    val features: List<GuildFeature>,
    @SerialName("approximate_member_count")
    val approximateMemberCount: Int,
    @SerialName("approximate_presence_count")
    val approximatePresenceCount: Int,
    val description: String?,
    val stickers: List<DiscordMessageSticker>,
)
