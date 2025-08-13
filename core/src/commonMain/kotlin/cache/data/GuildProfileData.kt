package dev.kord.core.cache.data

import dev.kord.common.entity.*
import kotlinx.serialization.Serializable

@Serializable
public data class GuildProfileData(
    val id: Snowflake,
    val name: String,
    val iconHash: String? = null,
    val memberCount: Int,
    val onlineCount: Int,
    val description: String,
    val brandColorPrimary: String,
    val bannerHash: String? = null,
    val gameApplicationIds: List<Snowflake>,
    val gameActivity: Map<Snowflake, DiscordGameActivity>,
    val tag: String? = null,
    val badge: GuildBadgeType,
    val badgeColorPrimary: String,
    val badgeColorSecondary: String,
    val badgeHash: String,
    val traits: List<DiscordGuildTrait>,
    val features: List<GuildFeature>,
    val customBannerHash: String? = null,
    val premiumSubscriptionCount: Int,
    val premiumTier: PremiumTier
) {
    public companion object {
        public fun from(entity: DiscordGuildProfile): GuildProfileData = with(entity) {
            GuildProfileData(
                id = id,
                name = name,
                iconHash = iconHash,
                memberCount = memberCount,
                onlineCount = onlineCount,
                description = description,
                brandColorPrimary = brandColorPrimary,
                bannerHash = bannerHash,
                gameApplicationIds = gameApplicationIds,
                gameActivity = gameActivity,
                tag = tag,
                badge = badge,
                badgeColorPrimary = badgeColorPrimary,
                badgeColorSecondary = badgeColorSecondary,
                badgeHash = badgeHash,
                traits = traits,
                features = features,
                customBannerHash = customBannerHash,
                premiumSubscriptionCount = premiumSubscriptionCount,
                premiumTier = premiumTier
            )
        }
    }
}

public fun DiscordGuildProfile.toData(): GuildProfileData = GuildProfileData.from(this)
