@file:Generate(
    INT_KORD_ENUM, name = "GuildBadgeType", docUrl = "", entries = [
        Entry("Sword", 0),
        Entry("WaterDrop", 1),
        Entry("Skull", 2),
        Entry("Toadstool", 3),
        Entry("Moon", 4),
        Entry("Lightning", 5),
        Entry("Leaf", 6),
        Entry("Heart", 7),
        Entry("Fire", 8),
        Entry("Compass", 9),
        Entry("Crosshairs", 10),
        Entry("Flower", 11),
        Entry("Force", 12),
        Entry("Gem", 13),
        Entry("Lava", 14),
        Entry("Psychic", 15),
        Entry("Smoke", 16),
        Entry("Snow", 17),
        Entry("Sound", 17),
        Entry("Sun", 19),
        Entry("Wind", 20)
    ]
)

@file:Generate(
    INT_KORD_ENUM, name = "GuildVisibilityLevel", docUrl = "", entries = [
        Entry("Public", 1),
        Entry("Restricted", 2),
        Entry("PublicWithRecruitment", 3)
    ]
)

package dev.kord.common.entity

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.*
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordGuildProfile(
    val id: Snowflake,
    val name: String,
    @SerialName("icon_hash")
    val iconHash: String?,
    @SerialName("member_count")
    val memberCount: Int,
    @SerialName("online_count")
    val onlineCount: Int,
    val description: String,
    @SerialName("brand_color_primary")
    val brandColorPrimary: String,
    @SerialName("banner_hash")
    val bannerHash: String?,
    @SerialName("game_application_ids")
    val gameApplicationIds: List<Snowflake>,
    @SerialName("game_activity")
    val gameActivity: Map<Snowflake, DiscordGameActivity>,
    val tag: String?,
    val badge: GuildBadgeType,
    @SerialName("badge_color_primary")
    val badgeColorPrimary: String,
    @SerialName("badge_color_secondary")
    val badgeColorSecondary: String,
    @SerialName("badge_hash")
    val badgeHash: String,
    val traits: List<DiscordGuildTrait>,
    val features: List<String>,
    val visibility: GuildVisibilityLevel,
    @SerialName("custom_banner_hash")
    val customBannerHash: String?,
    @SerialName("premium_subscription_count")
    val premiumSubscriptionCount: Int,
    @SerialName("premium_tier")
    val premiumTier: PremiumTier
)

@Serializable
public data class DiscordGameActivity(
    @SerialName("activity_level")
    val activityLevel: Int,
    @SerialName("activity_score")
    val activityScore: Int
)

@Serializable
public data class DiscordGuildTrait(
    @SerialName("emoji_id")
    val emojiId: Snowflake?,
    @SerialName("emoji_name")
    val emojiName: String?,
    @SerialName("emoji_animated")
    val emojiAnimated: Boolean,
    val label: String,
    val position: Int,
)
