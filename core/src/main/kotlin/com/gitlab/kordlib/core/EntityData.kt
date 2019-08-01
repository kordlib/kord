package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.*

data class RoleData(
        val id: String,
        val name: String? = null,
        val color: Int? = null,
        val hoist: Boolean? = null,
        val position: Int? = null,
        val permissions: Permissions? = null,
        val managed: Boolean? = null,
        val mentionable: Boolean? = null,
        val guildId: String? = null
)

data class GuildData(
        val id: String,
        val name: String? = null,
        val icon: String? = null,
        val splash: String? = null,
        val owner: Boolean? = null,
        val ownerId: String? = null,
        val permissions: Int? = null,
        val region: String? = null,
        val afkChannelId: String? = null,
        val afkTimeout: Int? = null,
        val embedEnabled: Boolean? = null,
        val embedChannelId: String? = null,
        val verificationLevel: VerificationLevel? = null,
        val defaultMessageNotifications: DefaultMessageNotificationLevel? = null,
        val explicitContentFilter: ExplicitContentFilter? = null,
        val roles: List<Role>? = null,
        val emojis: List<Emoji>? = null,
        val features: List<String>? = null,
        val mfaLevel: MFALevel? = null,
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
)

data class GuildIntegrationsData(
        val id: String,
        val name: String,
        val type: String,
        val enabled: Boolean,
        val syncing: Boolean,
        val roleId: String,
        val expireBehavior: Int,
        val gracePeriod: Int,
        val user: User,
        val account: IntegrationAccount,
        val syncedAt: String
)

data class GuildMemberData(
        val user: User? = null,
        val nick: String? = null,
        val roles: List<String>,
        val joinedAt: String,
        val deaf: Boolean,
        val mute: Boolean,
        val guildId: String
)

data class UserData(
        val id: String,
        val username: String,
        val discriminator: String,
        val avatar: String? = null,
        val bot: Boolean? = null,
        val mfaEnable: Boolean? = null,
        val locale: String? = null,
        val flags: Int? = null,
        val premiumType: Premium? = null,
        val verified: Boolean? = null,
        val email: String? = null
)

data class MessageData(
        val id: String,
        val channelId: String,
        val guildId: String? = null,
        val author: User? = null,
        val member: PartialGuildMember? = null,
        val content: String? = null,
        val timestamp: String? = null,
        val editedTimestamp: String? = null,
        val tts: Boolean? = null,
        val mentionEveryone: Boolean? = null,
        val mentions: List<OptionallyMemberUser>? = null,
        val mentionRoles: List<Role>? = null,
        val attachments: List<Attachment>? = null,
        val embeds: List<Embed>? = null,
        val reactions: List<Reaction>? = null,
        val nonce: String? = null,
        val pinned: Boolean? = null,
        val webhookId: String? = null,
        val type: MessageType? = null,
        val activity: MessageActivity? = null,
        val application: MessageApplication? = null
)


data class MessageApplicationData(
        val id: String,
        val coverImage: String? = null,
        val description: String,
        val icon: String,
        val name: String
)

data class OverwriteData(
        val id: String,
        val type: String,
        val allow: Int,
        val deny: Int
)

data class EmojiData(
        val id: String? = null,
        val name: String,
        val roles: List<String>? = null,
        val user: User? = null,
        val requireColons: Boolean? = null,
        val managed: Boolean? = null,
        val animated: Boolean? = null
)

data class WebhookData(
        val id: String,
        val guildId: String? = null,
        val channelId: String,
        val user: User? = null,
        val name: String? = null,
        val avatar: String? = null,
        val token: String
)

data class PresenceUserData(
        val id: String,
        val username: String? = null,
        val discriminator: String? = null,
        val avatar: String? = null,
        val bot: String? = null,
        val mfaEnable: String? = null,
        val locale: String? = null,
        val flags: String? = null,
        val premiumType: String? = null,
        val verified: String? = null,
        val email: String? = null
)
