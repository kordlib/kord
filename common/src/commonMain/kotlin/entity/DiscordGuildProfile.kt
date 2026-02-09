@file:Generate(
    INT_KORD_ENUM, name = "GuildBadgeType", docUrl = "https://docs.discord.food/resources/discovery#guild-badge-type", entries = [
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
        Entry("Wind", 20),
        Entry("Bunny", 21),
        Entry("Dog", 22),
        Entry("Frog", 23),
        Entry("Goat", 24),
        Entry("Cat", 25),
        Entry("Diamond", 26),
        Entry("Crown", 27),
        Entry("Trophy", 28),
        Entry("MoneyBag", 29),
        Entry("DollarSign", 30),
    ], isDiscordPreview = true
)

@file:Generate(
    INT_KORD_ENUM, name = "GuildVisibilityLevel", docUrl = "https://docs.discord.food/resources/discovery#guild-visibility", entries = [
        Entry("Public", 1),
        Entry("Restricted", 2),
        Entry("PublicWithRecruitment", 3)
    ], isDiscordPreview = true
)

package dev.kord.common.entity

import dev.kord.common.Color
import dev.kord.common.annotation.DiscordAPIPreview
import dev.kord.common.annotation.KordPreview
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.*
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A representation of a [Discord Guild Profile](https://docs.discord.food/resources/discovery#guild-profile-structure)
 *
 * @param id The ID of the guild
 * @param name The name of the guild (2-100 characters)
 * @param iconHash The guilds icon hash
 * @param memberCount Approximate count of guild members
 * @param onlineCount Approximate count of non-offline guild members
 * @param description The description for the guild (max 300 characters)
 * @param brandColorPrimary The guilds accent color as a hexadecimal color string
 * @param bannerHash The guilds clan banner hash
 * @param gameApplicationIds The IDs of the applications representing the games the guild plays (max 20)
 * @param gameActivity The activity of the guild in each game
 * @param tag The tag of the guild (2-4 characters)
 * @param badge The badge shown on the guilds tag
 * @param badgeColorPrimary The primary color of the badge as a hexadecimal color string
 * @param badgeColorSecondary The secondary color of the badge a hexadecimal color string
 * @param badgeHash The guild tag badge hash
 * @param traits a list of terms used to describe the guilds interest and personality (max 5)
 * @param features a list of enabled [GuildFeature]s
 * @param visibility The [GuildVisibilityLevel] for the guild
 * @param customBannerHash The guilds discovery splash hash
 * @param premiumSubscriptionCount The number of premium subscriptions (boosts) the guild currently has
 * @param premiumTier The guilds [PremiumTier] (boost level)
 */
@DiscordAPIPreview
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
    val brandColorPrimary: Color,
    @SerialName("banner_hash")
    val bannerHash: String?,
    @SerialName("game_application_ids")
    val gameApplicationIds: List<Snowflake>,
    @SerialName("game_activity")
    val gameActivity: Map<Snowflake, DiscordGameActivity>,
    val tag: String?,
    val badge: GuildBadgeType,
    @SerialName("badge_color_primary")
    val badgeColorPrimary: Color,
    @SerialName("badge_color_secondary")
    val badgeColorSecondary: Color,
    @SerialName("badge_hash")
    val badgeHash: String,
    val traits: List<DiscordGuildTrait>,
    val features: List<GuildFeature>,
    val visibility: GuildVisibilityLevel,
    @SerialName("custom_banner_hash")
    val customBannerHash: String?,
    @SerialName("premium_subscription_count")
    val premiumSubscriptionCount: Int,
    @SerialName("premium_tier")
    val premiumTier: PremiumTier
)

/**
 * A representation of a [Guild activity structure](https://docs.discord.food/resources/discovery#game-activity-structure)
 *
 * @param activityLevel The activity level of the guild in the game
 * @param activityScore The activity score of the guild in the game
 */
@KordPreview
@Serializable
public data class DiscordGameActivity(
    @SerialName("activity_level")
    val activityLevel: Int,
    @SerialName("activity_score")
    val activityScore: Int
)

/**
 * A representation of a [Discord guild trait structure](https://docs.discord.food/resources/discovery#guild-trait-structure)
 *
 * @param emojiId The ID of the emoji associated with the trait
 * @param emojiName The name of the emoji associated with the trait
 * @param emojiAnimated Whether the associated emoji is animated
 * @param label The name of the trait
 * @param position The position of the trait in the array for sorting
 */
@KordPreview
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
