package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public class PartialGuildData(
    public val id: Snowflake,
    public val name: Optional<String> = Optional.Missing(),
    public val icon: Optional<String>? = Optional.Missing(),
    @SerialName("icon_hash") public val iconHash: Optional<String?> = Optional.Missing(),
    public val splash: Optional<String?> = Optional.Missing(),
    @SerialName("discovery_splash") public val discoverySplash: Optional<String?> = Optional.Missing(),
    public val owner: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("owner_id") public val ownerId: OptionalSnowflake,
    public val permissions: Optional<Permissions> = Optional.Missing(),
    @SerialName("afk_channel_id") public val afkChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("afk_timeout") public val afkTimeout: Optional<DurationInSeconds> = Optional.Missing(),
    @SerialName("widget_enabled") public val widgetEnabled: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("widget_channel_id") public val widgetChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("verification_level") public val verificationLevel: Optional<VerificationLevel> = Optional.Missing(),
    @SerialName("default_message_notifications") public val defaultMessageNotifications: Optional<DefaultMessageNotificationLevel> = Optional.Missing(),
    @SerialName("explicit_content_filter") public val explicitContentFilter: Optional<ExplicitContentFilter> = Optional.Missing(),
    public val roles: List<Snowflake> = emptyList(),
    public val emojis: List<Snowflake> = emptyList(),
    public val features: List<GuildFeature> = emptyList(),
    @SerialName("mfa_level") public val mfaLevel: Optional<MFALevel> = Optional.Missing(),
    @SerialName("application_id") public val applicationId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("system_channel_id") public val systemChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("system_channel_flags") public val systemChannelFlags: Optional<SystemChannelFlags> = Optional.Missing(),
    @SerialName("rules_channel_id") public val rulesChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("joined_at") public val joinedAt: Optional<Instant> = Optional.Missing(),
    public val large: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("member_count") public val memberCount: OptionalInt = OptionalInt.Missing,
    @SerialName("voice_states") public val voiceStates: Optional<List<DiscordVoiceState>> = Optional.Missing(),
    public val members: Optional<List<Snowflake>> = Optional.Missing(),
    public val channels: Optional<List<Snowflake>> = Optional.Missing(),
    public val threads: Optional<List<ChannelData>> = Optional.Missing(),
    public val presences: Optional<List<DiscordPresenceUpdate>> = Optional.Missing(),
    @SerialName("max_presences") public val maxPresences: OptionalInt? = OptionalInt.Missing,
    @SerialName("max_members") public val maxMembers: OptionalInt = OptionalInt.Missing,
    @SerialName("vanity_url_code") public val vanityUrlCode: Optional<String>? = Optional.Missing(),
    public val description: Optional<String>? = Optional.Missing(),
    public val banner: Optional<String>? = Optional.Missing(),
    @SerialName("premium_tier") public val premiumTier: Optional<PremiumTier> = Optional.Missing(),
    @SerialName("premium_subscription_count") public val premiumSubscriptionCount: OptionalInt = OptionalInt.Missing,
    @SerialName("preferred_locale") public val preferredLocale: Optional<String> = Optional.Missing(),
    @SerialName("public_updates_channel_id") public val publicUpdatesChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("max_video_channel_users") public val maxVideoChannelUsers: OptionalInt = OptionalInt.Missing,
    @SerialName("approximate_member_count") public val approximateMemberCount: OptionalInt = OptionalInt.Missing,
    @SerialName("approximate_presence_count") public val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
    @SerialName("welcome_screen") public val welcomeScreen: Optional<WelcomeScreenData> = Optional.Missing(),
    @SerialName("nsfw_level") public val nsfwLevel: Optional<NsfwLevel> = Optional.Missing(),
    @SerialName("stage_instances")
    public val stageInstances: Optional<List<StageInstanceData>> = Optional.Missing(),
    public val stickers: Optional<List<StickerData>> = Optional.Missing(),
    @SerialName("guild_scheduled_events")
    public val guildScheduledEvents: Optional<List<GuildScheduledEventData>> = Optional.Missing(),
    @SerialName("premium_progress_bar_enabled")
    public val premiumProgressBarEnabled: OptionalBoolean = OptionalBoolean.Missing

) {
    public companion object {
        public fun from(partialGuild: DiscordPartialGuild): PartialGuildData = with(partialGuild) {
            PartialGuildData(
    id = id,
                name = name,
                icon = icon,
                iconHash = iconHash,
                splash = splash,
                discoverySplash = discoverySplash,
                //owner = owner,
                ownerId = ownerId,
                permissions = permissions,
                afkChannelId = afkChannelId,
                afkTimeout = afkTimeout,
                widgetEnabled = widgetEnabled,
                widgetChannelId = widgetChannelId,
                verificationLevel = verificationLevel,
                defaultMessageNotifications = defaultMessageNotifications,
                explicitContentFilter = explicitContentFilter,
                roles = roles.map { it.id },
                emojis = emojis.map { it.id!! },
                features = features,
                mfaLevel = mfaLevel,
                applicationId = applicationId,
                systemChannelId = systemChannelId,
                systemChannelFlags = systemChannelFlags,
                rulesChannelId = rulesChannelId,
                joinedAt = joinedAt,
                large = large,
                memberCount = memberCount,
                channels = channels.mapList { it.id },
                maxPresences = maxPresences,
                maxMembers = maxMembers,
                vanityUrlCode = vanityUrlCode,
                description = description,
                banner = banner,
                premiumTier = premiumTier,
                premiumSubscriptionCount = premiumSubscriptionCount,
                preferredLocale = preferredLocale,
                publicUpdatesChannelId = publicUpdatesChannelId,
                maxVideoChannelUsers = maxVideoChannelUsers,
                approximateMemberCount = approximateMemberCount,
                approximatePresenceCount = approximatePresenceCount,
                welcomeScreen = welcomeScreen.map { WelcomeScreenData.from(it) },
                nsfwLevel = nsfwLevel,
                threads = threads.mapList { it.toData() },
                stageInstances = stageInstances.mapList { StageInstanceData.from(it) },
                stickers = stickers.mapList { StickerData.from(it) },
                guildScheduledEvents = guildScheduledEvents.mapList { GuildScheduledEventData.from(it) },
                premiumProgressBarEnabled = premiumProgressBarEnabled
            )
        }
    }
}
