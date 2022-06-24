package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

private val MessageData.nullableGuildId get() = guildId.value
private val ChannelData.nullableGuildId get() = guildId.value
private val WebhookData.nullableGuildId get() = guildId.value

@Serializable
public data class GuildData(
    val id: Snowflake,
    val name: String,
    val icon: String? = null,
    val iconHash: Optional<String?> = Optional.Missing(),
    val splash: Optional<String?> = Optional.Missing(),
    val discoverySplash: Optional<String?> = Optional.Missing(),
    //val owner: OptionalBoolean = OptionalBoolean.Missing, useless?
    val ownerId: Snowflake,
    val permissions: Optional<Permissions> = Optional.Missing(),
    @Deprecated("The region field has been moved to Channel#rtcRegion in Discord API v9", ReplaceWith("ChannelData#rtcRegion"))
    val region: String,
    val afkChannelId: Snowflake? = null,
    val afkTimeout: DurationInSeconds,
    val widgetEnabled: OptionalBoolean = OptionalBoolean.Missing,
    val widgetChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    val verificationLevel: VerificationLevel,
    val defaultMessageNotifications: DefaultMessageNotificationLevel,
    val explicitContentFilter: ExplicitContentFilter,
    val roles: List<Snowflake>,
    val emojis: List<Snowflake>,
    val features: List<GuildFeature>,
    val mfaLevel: MFALevel,
    val applicationId: Snowflake? = null,
    val systemChannelId: Snowflake? = null,
    val systemChannelFlags: SystemChannelFlags,
    val rulesChannelId: Snowflake? = null,
    val joinedAt: Optional<Instant> = Optional.Missing(),
    val large: OptionalBoolean = OptionalBoolean.Missing,
    //val unavailable: OptionalBoolean = OptionalBoolean.Missing, useless?
    val memberCount: OptionalInt = OptionalInt.Missing,
//        val members: Optional<List<Snowflake>> = Optional.Missing(),
    val channels: Optional<List<Snowflake>> = Optional.Missing(),
    val maxPresences: OptionalInt? = OptionalInt.Missing,
    val maxMembers: OptionalInt = OptionalInt.Missing,
    val vanityUrlCode: String? = null,
    val description: String? = null,
    val banner: String? = null,
    val premiumTier: PremiumTier,
    val premiumSubscriptionCount: OptionalInt = OptionalInt.Missing,
    val preferredLocale: String,
    val publicUpdatesChannelId: Snowflake? = null,
    val maxVideoChannelUsers: OptionalInt = OptionalInt.Missing,
    val approximateMemberCount: OptionalInt = OptionalInt.Missing,
    val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
    val welcomeScreen: Optional<WelcomeScreenData> = Optional.Missing(),
    val nsfwLevel: NsfwLevel,
    val threads: Optional<List<ChannelData>> = Optional.Missing(),
    val stageInstances: Optional<List<StageInstanceData>> = Optional.Missing(),
    val stickers: Optional<List<StickerData>> = Optional.Missing(),
    val guildScheduledEvents: Optional<List<GuildScheduledEventData>> = Optional.Missing(),
    val premiumProgressBarEnabled: Boolean
) {
    public companion object {

        public val description: DataDescription<GuildData, Snowflake> = description(GuildData::id) {

            link(GuildData::id to RoleData::guildId)
            link(GuildData::id to ChannelData::nullableGuildId)
            link(GuildData::id to MemberData::guildId)
            link(GuildData::id to MessageData::nullableGuildId)
            link(GuildData::id to WebhookData::nullableGuildId)
            link(GuildData::id to VoiceStateData::guildId)
            link(GuildData::id to PresenceData::guildId)
        }

        public fun from(entity: DiscordGuild): GuildData = with(entity) {
            GuildData(
                id = id,
                name = name,
                icon = icon,
                iconHash = iconHash,
                splash = splash,
                discoverySplash = discoverySplash,
                //owner = owner,
                ownerId = ownerId,
                permissions = permissions,
                region = @Suppress("DEPRECATION") region,
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
//                    unavailable = unavailable,
                memberCount = memberCount,
//                    members = members.mapList { it.user.value!!.id },
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

public fun DiscordGuild.toData(): GuildData = GuildData.from(this)
