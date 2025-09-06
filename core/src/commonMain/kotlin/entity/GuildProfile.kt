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

    /**
     * The name of the guild
     */
    public val name: String get() = data.name

    /**
     * The icon hash, if present
     */
    public val iconHash: String? get() = data.iconHash

    public val icon: Asset? get() = iconHash?.let { Asset.guildIcon(id, it, kord) }

    /**
     * The approximate member count
     */
    public val memberCount: Int get() = data.memberCount

    /**
     * The approximate non-offline member count
     */
    public val onlineCount: Int get() = data.onlineCount

    /**
     * The guild description
     */
    public val description: String get() = data.description

    /**
     * The hexadecimal color code for the guild accent
     */
    public val primaryBrandColorCode: String get() = data.brandColorPrimary

    /**
     * The guild accent color as a [Color]
     */
    public val primaryBrandColor: Color get() = Color(primaryBrandColorCode.hexToInt())

    /**
     * The banner hash, if present
     */
    public val bannerHash: String? get() = data.bannerHash

    public val banner: Asset? get() = bannerHash?.let { Asset.guildBanner(id, it, kord) }

    /**
     * The IDs of the applications representing the games the guild plays
     */
    public val gameApplicationIds: Set<Snowflake> get() = data.gameApplicationIds.toSet()

    /**
     * The [GameActivity] of the guild in each game
     */
    public val gameActivity: Map<Snowflake, GameActivity> get() {
        val map: MutableMap<Snowflake, GameActivity> = mutableMapOf()
        data.gameActivity.forEach {
            map[it.key] = GameActivity(GameActivityData.from(it.value), kord)
        }
        return map
    }

    /**
     * The tag of the guild
     */
    public val tag: String? get() = data.tag

    /**
     * The [GuildBadgeType] for the guild
     */
    public val badgeType: GuildBadgeType get() = data.badge

    /**
     * The hexadecimal color code for the badges primary color
     */
    public val primaryBadgeColorCode: String get() = data.badgeColorPrimary

    /**
     * The primary badge color as [Color]
     */
    public val primaryBadgeColor: Color get() = Color(primaryBadgeColorCode.hexToInt())

    /**
     * The hexadecimal color code for the badges secondary color
     */
    public val secondaryBadgeColorCode: String get() = data.badgeColorSecondary

    /**
     * The secondary badge color as [Color]
     */
    public val secondaryBadgeColor: Color get() = Color(secondaryBadgeColorCode.hexToInt())

    /**
     * The guild tag badge hash
     */
    public val badgeHash: String get() = data.badgeHash

    public val badge: Asset get() = Asset.guildTagBadge(id, badgeHash, kord)

    /**
     * A list of [GuildTrait]s, used to describe the guild's interests and personality
     */
    public val traits: List<GuildTrait> get() {
        val list: MutableList<GuildTrait> = mutableListOf()
        data.traits.forEach { list.add(GuildTrait(GuildTraitData.from(it), kord)) }
        return list
    }

    /**
     * A list of enabled guild features, restricted to community features.
     */
    public val features: List<GuildFeature> get() = data.features

    /**
     * The guilds discovery badge hash
     */
    public val customBannerHash: String? get() = data.customBannerHash

    public val customBanner: Asset? get() = customBannerHash?.let { Asset.guildDiscoverySplash(id, it, kord) }

    /**
     * The number of boosts the guild currently has
     */
    public val premiumSubscriptionCount: Int get() = data.premiumSubscriptionCount

    /**
     * The guilds boost level
     */
    public val premiumTier: PremiumTier get() = data.premiumTier
}