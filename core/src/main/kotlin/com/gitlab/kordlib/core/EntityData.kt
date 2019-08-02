package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
        val id: String,
        var name: String? = null,
        var color: Int? = null,
        var hoist: Boolean? = null,
        var position: Int? = null,
        var permissions: Permissions? = null,
        var managed: Boolean? = null,
        var mentionable: Boolean? = null
)

@Serializable
data class GuildData(
        val id: String,
        var name: String? = null,
        var icon: String? = null,
        var splash: String? = null,
        var owner: Boolean? = null,
        @SerialName("owner_id")
        var ownerId: String? = null,
        var permissions: Int? = null,
        var region: String? = null,
        @SerialName("afk_channel_id")
        var afkChannelId: String? = null,
        @SerialName("afk_timeout")
        var afkTimeout: Int? = null,
        @SerialName("embed_enabled")
        var embedEnabled: Boolean? = null,
        @SerialName("embed_channel_id")
        var embedChannelId: String? = null,
        @SerialName("verification_level")
        var verificationLevel: VerificationLevel? = null,
        @SerialName("default_message_notifications")
        var defaultMessageNotifications: DefaultMessageNotificationLevel? = null,
        @SerialName("explicit_content_filter")
        var explicitContentFilter: ExplicitContentFilter? = null,
        var roles: List<Role>? = null,
        var emojis: List<Emoji>? = null,
        var features: List<String>? = null,
        @SerialName("mfa_level")
        var mfaLevel: MFALevel? = null,
        @SerialName("application_id")
        var applicationId: String? = null,
        @SerialName("widget_enabled")
        var widgetEnabled: Boolean? = null,
        @SerialName("widget_channel_id")
        var widgetChannelId: String? = null,
        @SerialName("system_channel_id")
        var systemChannelId: String? = null,
        @SerialName("joined_at")
        var joinedAt: String? = null,
        var large: Boolean? = null,
        var unavailable: Boolean? = null,
        @SerialName("member_count")
        var memberCount: Int? = null,
        @SerialName("voice_states")
        var voiceStates: List<VoiceState>? = null,
        var members: List<GuildMember>? = null,
        var channels: List<Channel>? = null,
        var presences: List<PresenceUpdateData>? = null,
        @SerialName("max_presences")
        var maxPresences: Int? = null,
        @SerialName("max_members")
        var maxMembers: Int? = null,
        @SerialName("vanity_url_code")
        var vanityUrlCode: String? = null,
        var description: String? = null,
        var banner: String? = null
)


@Serializable
data class GuildIntegrationsData(
        val id: String,
        var name: String,
        var type: String,
        var enabled: Boolean,
        var syncing: Boolean,
        @SerialName("role_id")
        var roleId: String,
        @SerialName("expire_behavior")
        var expireBehavior: Int,
        @SerialName("expire_grace_period")
        var gracePeriod: Int,
        var user: User,
        var account: IntegrationAccount,
        @SerialName("synced_at")
        var syncedAt: String
)

@Serializable
data class GuildMemberData(
        var user: User,
        var nick: String? = null,
        var roles: List<String>? = null,
        @SerialName("joined_at")
        var joinedAt: String? = null,
        var deaf: Boolean? = null,
        var mute: Boolean? = null,
        @SerialName("guild_id")
        var guildId: String? = null
)


@Serializable
data class UserData(
        val id: String,
        var username: String,
        var discriminator: String,
        var avatar: String? = null,
        var bot: Boolean? = null,
        @SerialName("mfa_enable")
        var mfaEnable: Boolean? = null,
        var locale: String? = null,
        var flags: Int? = null,
        @SerialName("premium_type")
        var premiumType: Premium? = null,
        var verified: Boolean? = null,
        var email: String? = null
)

@Serializable
data class MessageData(
        val id: String,
        @SerialName("channel_id")
        var channelId: String,
        @SerialName("guild_id")
        var guildId: String? = null,
        var author: User? = null,
        var member: PartialGuildMember? = null,
        var content: String? = null,
        var timestamp: String? = null,
        @SerialName("edited_timestamp")
        var editedTimestamp: String? = null,
        var tts: Boolean? = null,
        @SerialName("mention_everyone")
        var mentionEveryone: Boolean? = null,
        var mentions: List<OptionallyMemberUser>? = null,
        @SerialName("mention_roles")
        var mentionRoles: List<Role>? = null,
        var attachments: List<Attachment>? = null,
        var embeds: List<Embed>? = null,
        var reactions: List<Reaction>? = null,
        var nonce: String? = null,
        var pinned: Boolean? = null,
        @SerialName("webhook_id")
        var webhookId: String? = null,
        var type: MessageType? = null,
        var activity: MessageActivity? = null,
        var application: MessageApplication? = null
)


@Serializable
data class MessageApplicationData(
        val id: String,
        @SerialName("cover_image")
        var coverImage: String? = null,
        var description: String,
        var icon: String,
        var name: String
)


@Serializable
data class OverwriteData(
        val id: String,
        var type: String,
        var allow: Int,
        var deny: Int
)

@Serializable
data class EmojiData(
        val id: String? = null,
        var name: String,
        var roles: List<String>? = null,
        var user: User? = null,
        @SerialName("require_colons")
        var requireColons: Boolean? = null,
        var managed: Boolean? = null,
        var animated: Boolean? = null
)


@Serializable
data class WebhookData(
        val id: String,
        @SerialName("guild_id")
        var guildId: String? = null,
        var channelId: String,
        var user: User? = null,
        var name: String? = null,
        var avatar: String? = null,
        var token: String
)


@Serializable
data class PresenceUserData(
        val id: String,
        var username: String? = null,
        var discriminator: String? = null,
        var avatar: String? = null,
        var bot: String? = null,
        @SerialName("mfa_enable")
        var mfaEnable: String? = null,
        var locale: String? = null,
        var flags: String? = null,
        @SerialName("premium_type")
        var premiumType: Premium? = null,
        var verified: String? = null,
        var email: String? = null
)
