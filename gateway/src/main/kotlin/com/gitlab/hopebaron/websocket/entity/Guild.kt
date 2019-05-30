package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnavailableGuild(
        val id: String,
        val unavailable: Boolean?
)

@Serializable
data class Guild(
        val id: String,
        val name: String,
        val icon: String?,
        val splash: String?,
        val owner: Boolean?,
        @SerialName("owner_id")
        val ownerId: String,
        val permissions: Int?,
        val region: String,
        @SerialName("afk_channel_id")
        val afkChannelId: String?,
        @SerialName("afk_timeout")
        val afkTimeout: Int,
        @SerialName("embed_enabled")
        val embedEnabled: Boolean?,
        @SerialName("embed_channel_id")
        val embedChannelId: String?,
        @SerialName("verification_level")
        val verificationLevel: Int,
        @SerialName("default_message_notifications")
        val defaultMessageNotifications: Int,
        @SerialName("explicit_content_filter")
        val explicitContentFilter: Int,
        val roles: List<Role>,
        val emojis: List<Emoji>,
        val features: List<String>,
        @SerialName("mfa_level")
        val mfaLevel: Int,
        @SerialName("application_id")
        val applicationId: String?,
        @SerialName("widget_enabled")
        val widgetEnabled: Boolean?,
        @SerialName("widget_channel_id")
        val widgetChannelId: String?,
        @SerialName("system_channel_id")
        val systemChannelId: String?,
        @SerialName("joined_at")
        val joinedAt: String?,
        val large: Boolean?,
        val unavailable: Boolean?,
        @SerialName("member_count")
        val memberCount: Int?,
        @SerialName("voice_states")
        val voiceStates: List<VoiceState>?,
        val members: List<GuildMember>?,
        val channels: List<Channel>?,
        val presences: List<PresenceUpdateData>?,
        @SerialName("max_presences")
        val maxPresences: Int?,
        @SerialName("max_members")
        val maxMembers: Int?,
        @SerialName("vanity_url_code")
        val vanityUrlCode: String?,
        val description: String?,
        val banner: String?
)

@Serializable
data class GuildBan(
        @SerialName("guild_id")
        val guildId: String,
        val user: User
)

@Serializable
data class GuildIntegrations(@SerialName("guild_id") val guildId: String)

@Serializable
data class GuildMembersChunkData(
        @SerialName("guild_id")
        val guildId: String,
        val members: List<GuildMember>
)

@Serializable
data class VoiceServerUpdateData(
        val token: String,
        @SerialName("guild_id")
        val guildId: String,
        val endpoint: String
)

@Serializable
data class WebhooksUpdateData(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String
)

@Serializable
data class VoiceState(
        @SerialName("guild_id")
        val guildId: String?,
        @SerialName("channel_id")
        val channelId: String?,
        @SerialName("user_id")
        val userId: String,
        @SerialName("guild_member")
        val member: GuildMember?,
        @SerialName("session_id")
        val sessionId: String,
        val deaf: Boolean,
        val mute: Boolean,
        @SerialName("self_deaf")
        val selfDeaf: Boolean,
        @SerialName("self_mute")
        val selfMute: Boolean,
        val suppress: Boolean
)