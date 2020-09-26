package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.Serializable

@Serializable
data class GuildData(
        val id: Long,
        val name: String,
        val icon: String? = null,
        val splash: String? = null,
        val owner: Boolean? = null,
        val ownerId: Long,
        val permissions: Permissions? = null,
        val region: String,
        val afkChannelId: Long? = null,
        val afkTimeout: Int,
        //TODO, keep this?
        val embedEnabled: Boolean? = null,
        val embedChannelId: Long? = null,
        val verificationLevel: VerificationLevel,
        val defaultMessageNotifications: DefaultMessageNotificationLevel,
        val explicitContentFilter: ExplicitContentFilter,
        val roles: List<Long>,
        val emojis: List<EmojiData>,
        val features: List<GuildFeature>,
        val mfaLevel: MFALevel,
        val applicationId: Long? = null,
        val widgetEnabled: Boolean? = null,
        val widgetChannelId: Long? = null,
        val systemChannelId: Long? = null,
        val joinedAt: String? = null,
        val large: Boolean? = null,
        val memberCount: Int? = null,
        val voiceStates: List<VoiceStateData> = emptyList(),
        val members: List<MemberData> = emptyList(),
        val channels: List<Long> = emptyList(),
        val presences: List<PresenceData> = emptyList(),
        val maxPresences: Int? = null,
        val maxMembers: Int? = null,
        val vanityUrlCode: String? = null,
        val description: String? = null,
        val banner: String? = null,
        val premiumTier: PremiumTier,
        val systemChannelFlags: SystemChannelFlags? = null,
        val rulesChannelId: Long? = null,
        val discoverySplash: String? = null,
        val publicUpdatesChannelId: Long? = null,
        val preferredLocale: String,

        /**
         * Approximate number of members in this guild,
         * returned from the GET /guild/<id> endpoint when with_counts is true
         */
        val approximateMemberCount: Int? = null,

        /**
         * Approximate number of online members in this guild,
         * returned from the GET /guild/<id> endpoint when with_counts is true
         */
        val approximatePresenceCount: Int? = null
) {
    companion object {

        val description = description(GuildData::id) {
            link(GuildData::id to RoleData::guildId)
            link(GuildData::id to ChannelData::guildId)
            link(GuildData::id to MemberData::guildId)
            @Suppress("DEPRECATION")
            link(GuildData::id to MessageData::guildId)
            link(GuildData::id to WebhookData::guildId)
            link(GuildData::id to VoiceStateData::guildId)
            link(GuildData::id to PresenceData::guildId)
        }

        fun from(entity: DiscordGuild) = with(entity) {
            GuildData(
                    id.toLong(),
                    name,
                    icon,
                    splash,
                    owner,
                    ownerId.toLong(),
                    permissions,
                    region,
                    afkChannelId?.toLong(),
                    afkTimeout,
                    embedEnabled,
                    embedChannelId?.toLong(),
                    verificationLevel,
                    defaultMessageNotifications,
                    explicitContentFilter,
                    roles.map { it.id.toLong() },
                    emojis.map { EmojiData.from(guildId = id, id = it.id!!, entity = it) },
                    features,
                    mfaLevel,
                    applicationId?.toLong(),
                    widgetEnabled,
                    widgetChannelId?.toLong(),
                    systemChannelId?.toLong(),
                    joinedAt,
                    large,
                    memberCount,
                    voiceStates.orEmpty().map { VoiceStateData.from(id, it) },
                    members.orEmpty().map { MemberData.from(userId = it.user!!.id, guildId = id, entity = it) },
                    channels.orEmpty().map { it.id.toLong() },
                    presences.orEmpty().map { PresenceData.from(id, it) },
                    maxPresences,
                    maxMembers,
                    vanityUrlCode,
                    description,
                    banner,
                    premiumTier,
                    systemChannelFlags,
                    rulesChannelId?.toLong(),
                    discoverySplash,
                    publicUpdatesChannelId?.toLong(),
                    preferredLocale,
                    approximateMemberCount = approximateMemberCount,
                    approximatePresenceCount = approximatePresenceCount
            )
        }
    }
}

fun DiscordGuild.toData() = GuildData.from(this)