package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.*

data class RoleData(
        val id: String,
        var name: String? = null,
        var color: Int? = null,
        var hoist: Boolean? = null,
        var position: Int? = null,
        var permissions: Permissions? = null,
        var managed: Boolean? = null,
        var mentionable: Boolean? = null,
        var guildId: String? = null
)

data class GuildData(
        val id: String,
        var name: String? = null,
        var icon: String? = null,
        var splash: String? = null,
        var owner: Boolean? = null,
        var ownerId: String? = null,
        var permissions: Int? = null,
        var region: String? = null,
        var afkChannelId: String? = null,
        var afkTimeout: Int? = null,
        var embedEnabled: Boolean? = null,
        var embedChannelId: String? = null,
        var verificationLevel: VerificationLevel? = null,
        var defaultMessageNotifications: DefaultMessageNotificationLevel? = null,
        var explicitContentFilter: ExplicitContentFilter? = null,
        var roles: List<Role>? = null,
        var emojis: List<Emoji>? = null,
        var features: List<String>? = null,
        var mfaLevel: MFALevel? = null,
        var applicationId: String? = null,
        var widgetEnabled: Boolean? = null,
        var widgetChannelId: String? = null,
        var systemChannelId: String? = null,
        var joinedAt: String? = null,
        var large: Boolean? = null,
        var unavailable: Boolean? = null,
        var memberCount: Int? = null,
        var voiceStates: List<VoiceState>? = null,
        var members: List<GuildMember>? = null,
        var channels: List<Channel>? = null,
        var presences: List<PresenceUpdateData>? = null,
        var maxPresences: Int? = null,
        var maxMembers: Int? = null,
        var vanityUrlCode: String? = null,
        var description: String? = null,
        var banner: String? = null
)

data class GuildIntegrationsData(
        val id: String,
        var name: String,
        var type: String,
        var enabled: Boolean,
        var syncing: Boolean,
        var roleId: String,
        var expireBehavior: Int,
        var gracePeriod: Int,
        var user: User,
        var account: IntegrationAccount,
        var syncedAt: String
)

data class GuildMemberData(
        var user: User? = null,
        var nick: String? = null,
        var roles: List<String>,
        var joinedAt: String,
        var deaf: Boolean,
        var mute: Boolean,
        var guildId: String
)

data class UserData(
        val id: String,
        var username: String,
        var discriminator: String,
        var avatar: String? = null,
        var bot: Boolean? = null,
        var mfaEnable: Boolean? = null,
        var locale: String? = null,
        var flags: Int? = null,
        var premiumType: Premium? = null,
        var verified: Boolean? = null,
        var email: String? = null
)

data class MessageData(
        val id: String,
        var channelId: String,
        var guildId: String? = null,
        var author: User? = null,
        var member: PartialGuildMember? = null,
        var content: String? = null,
        var timestamp: String? = null,
        var editedTimestamp: String? = null,
        var tts: Boolean? = null,
        var mentionEveryone: Boolean? = null,
        var mentions: List<OptionallyMemberUser>? = null,
        var mentionRoles: List<Role>? = null,
        var attachments: List<Attachment>? = null,
        var embeds: List<Embed>? = null,
        var reactions: List<Reaction>? = null,
        var nonce: String? = null,
        var pinned: Boolean? = null,
        var webhookId: String? = null,
        var type: MessageType? = null,
        var activity: MessageActivity? = null,
        var application: MessageApplication? = null
)


data class MessageApplicationData(
        val id: String,
        var coverImage: String? = null,
        var description: String,
        var icon: String,
        var name: String
)

data class OverwriteData(
        val id: String,
        var type: String,
        var allow: Int,
        var deny: Int
)

data class EmojiData(
        val id: String? = null,
        var name: String,
        var roles: List<String>? = null,
        var user: User? = null,
        var requireColons: Boolean? = null,
        var managed: Boolean? = null,
        var animated: Boolean? = null
)

data class WebhookData(
        val id: String,
        var guildId: String? = null,
        var channelId: String,
        var user: User? = null,
        var name: String? = null,
        var avatar: String? = null,
        var token: String
)

data class PresenceUserData(
        val id: String,
        var username: String? = null,
        var discriminator: String? = null,
        var avatar: String? = null,
        var bot: String? = null,
        var mfaEnable: String? = null,
        var locale: String? = null,
        var flags: String? = null,
        var premiumType: String? = null,
        var verified: String? = null,
        var email: String? = null
)
