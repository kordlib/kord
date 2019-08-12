package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.Serializable

@Serializable
data class GuildData(
        val id: String,
        val name: String,
        val icon: String? = null,
        val splash: String? = null,
        val owner: Boolean? = null,
        val ownerId: String,
        val permissions: Permissions? = null,
        val region: String,
        val afkChannelId: String? = null,
        val afkTimeout: Int,
        val embedEnabled: Boolean? = null,
        val embedChannelId: String? = null,
        val verificationLevel: VerificationLevel,
        val defaultMessageNotifications: DefaultMessageNotificationLevel,
        val explicitContentFilter: ExplicitContentFilter,
        val roles: List<Role>,
        val emojis: List<Emoji>,
        val features: List<String>,
        val mfaLevel: MFALevel,
        val applicationId: String? = null,
        val widgetEnabled: Boolean? = null,
        val widgetChannelId: String? = null,
        val systemChannelId: String? = null,
        val joinedAt: String? = null,
        val large: Boolean? = null,
        val unavailable: Boolean? = null,
        val memberCount: Int? = null,
        val voiceStates: List<VoiceState>? = null,
        val members: List<GuildMember>? = null,
        val channels: List<Channel>? = null,
        val presences: List<PresenceUpdateData>? = null,
        val maxPresences: Int? = null,
        val maxMembers: Int? = null,
        val vanityUrlCode: String? = null,
        val description: String? = null,
        val banner: String? = null
) {
    companion object {

        val description = description(GuildData::id) {
            link(GuildData::id to RoleData::guildId)
            link(GuildData::id to ChannelData::guildId)
            link(GuildData::id to GuildMemberData::guildId)
            link(GuildData::id to MessageData::guildId)
            link(GuildData::id to WebhookData::guildId)
        }

        fun from(entity: Guild) = with(entity) {
            GuildData(
                    id,
                    name,
                    icon,
                    splash,
                    owner,
                    ownerId,
                    permissions,
                    region,
                    afkChannelId,
                    afkTimeout,
                    embedEnabled,
                    embedChannelId,
                    verificationLevel,
                    defaultMessageNotifications,
                    explicitContentFilter,
                    roles,
                    emojis,
                    features,
                    mfaLevel,
                    applicationId,
                    widgetEnabled,
                    widgetChannelId,
                    systemChannelId,
                    joinedAt,
                    large,
                    unavailable,
                    memberCount,
                    voiceStates,
                    members,
                    channels,
                    presences,
                    maxPresences,
                    maxMembers,
                    vanityUrlCode,
                    description,
                    banner
            )
        }
    }
}