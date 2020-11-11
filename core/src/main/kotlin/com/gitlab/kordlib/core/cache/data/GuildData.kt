package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.common.entity.optional.*
import kotlinx.serialization.Serializable

private val MessageData.nullableGuildId get() = guildId.value
private val ChannelData.nullableGuildId get() = guildId.value
private val WebhookData.nullableGuildId get() = guildId.value

@Serializable
data class GuildData(
        val id: Snowflake,
        val name: String,
        val icon: String?,
        val iconHash: Optional<String?> = Optional.Missing(),
        val splash: Optional<String?> = Optional.Missing(),
        val discoverySplash: Optional<String?> = Optional.Missing(),
        //val owner: OptionalBoolean = OptionalBoolean.Missing, useless?
        val ownerId: Snowflake,
        val permissions: Optional<Permissions> = Optional.Missing(),
        val region: String,
        val afkChannelId: Snowflake?,
        val afkTimeout: Int,
        val widgetEnabled: OptionalBoolean = OptionalBoolean.Missing,
        val widgetChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
        val verificationLevel: VerificationLevel,
        val defaultMessageNotifications: DefaultMessageNotificationLevel,
        val explicitContentFilter: ExplicitContentFilter,
        val roles: List<Snowflake>,
        val emojis: List<Snowflake>,
        val features: List<GuildFeature>,
        val mfaLevel: MFALevel,
        val applicationId: Snowflake?,
        val systemChannelId: Snowflake?,
        val systemChannelFlags: SystemChannelFlags,
        val rulesChannelId: Snowflake?,
        val joinedAt: Optional<String> = Optional.Missing(),
        val large: OptionalBoolean = OptionalBoolean.Missing,
        //val unavailable: OptionalBoolean = OptionalBoolean.Missing, useless?
        val memberCount: OptionalInt = OptionalInt.Missing,
//        val members: Optional<List<Snowflake>> = Optional.Missing(),
        val channels: Optional<List<Snowflake>> = Optional.Missing(),
        val maxPresences: OptionalInt? = OptionalInt.Missing,
        val maxMembers: OptionalInt = OptionalInt.Missing,
        val vanityUrlCode: String?,
        val description: String?,
        val banner: String?,
        val premiumTier: PremiumTier,
        val premiumSubscriptionCount: OptionalInt = OptionalInt.Missing,
        val preferredLocale: String,
        val publicUpdatesChannelId: Snowflake?,
        val maxVideoChannelUsers: OptionalInt = OptionalInt.Missing,
        val approximateMemberCount: OptionalInt = OptionalInt.Missing,
        val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
) {
    companion object {

        val description = description(GuildData::id) {

            link(GuildData::id to RoleData::guildId)
            link(GuildData::id to ChannelData::nullableGuildId)
            link(GuildData::id to MemberData::guildId)
            link(GuildData::id to MessageData::nullableGuildId)
            link(GuildData::id to WebhookData::nullableGuildId)
            link(GuildData::id to VoiceStateData::guildId)
            link(GuildData::id to PresenceData::guildId)
        }

        fun from(entity: DiscordGuild) = with(entity) {
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
                    region = region,
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
            )
        }
    }
}

fun DiscordGuild.toData() = GuildData.from(this)