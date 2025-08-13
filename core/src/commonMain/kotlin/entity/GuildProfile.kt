package dev.kord.core.entity

import dev.kord.common.Color
import dev.kord.common.entity.GuildBadgeType
import dev.kord.common.entity.GuildFeature
import dev.kord.common.entity.PremiumTier
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.GameActivityData
import dev.kord.core.cache.data.GuildProfileData
import dev.kord.core.cache.data.GuildTraitData

@OptIn(ExperimentalStdlibApi::class)
public class GuildProfile(
    public val data: GuildProfileData,
    override val kord: Kord
) : KordEntity {
    override val id: Snowflake get() = data.id

    public val name: String get() = data.name

    public val iconHash: String? get() = data.iconHash

    public val icon: Asset? get() = iconHash?.let { Asset.guildIcon(id, it, kord) }

    public val memberCount: Int get() = data.memberCount

    public val onlineCount: Int get() = data.onlineCount

    public val description: String get() = data.description

    public val primaryBrandColorCode: String get() = data.brandColorPrimary

    public val primaryBrandColor: Color get() = Color(primaryBrandColorCode.hexToInt())

    public val bannerHash: String? get() = data.bannerHash

    public val banner: Asset? get() = bannerHash?.let { Asset.guildBanner(id, it, kord) }

    public val gameApplicationIds: Set<Snowflake> get() = data.gameApplicationIds.toSet()

    public val gameActivity: Map<Snowflake, GameActivity> get() {
        val map: MutableMap<Snowflake, GameActivity> = mutableMapOf()
        data.gameActivity.forEach {
            map[it.key] = GameActivity(GameActivityData.from(it.value), kord)
        }
        return map
    }

    public val tag: String? get() = data.tag

    public val badgeType: GuildBadgeType get() = data.badge

    public val primaryBadgeColorCode: String get() = data.badgeColorPrimary

    public val primaryBadgeColor: Color get() = Color(primaryBadgeColorCode.hexToInt())

    public val secondaryBadgeColorCode: String get() = data.badgeColorSecondary

    public val secondaryBadgeColor: Color get() = Color(secondaryBadgeColorCode.hexToInt())

    public val badgeHash: String get() = data.badgeHash

    public val badge: Asset get() = Asset.guildTagBadge(id, badgeHash, kord)

    public val traits: List<GuildTrait> get() {
        val list: MutableList<GuildTrait> = mutableListOf()
        data.traits.forEach { list.add(GuildTrait(GuildTraitData.from(it), kord)) }
        return list
    }

    public val features: List<GuildFeature> get() = data.features

    public val customBannerHash: String? get() = data.customBannerHash

    public val customBanner: Asset? get() = customBannerHash?.let { Asset.guildDiscoverySplash(id, it, kord) }

    public val premiumSubscriptionCount: Int get() = data.premiumSubscriptionCount

    public val premiumTier: PremiumTier get() = data.premiumTier
}