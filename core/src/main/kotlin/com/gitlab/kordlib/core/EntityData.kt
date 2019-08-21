package com.gitlab.kordlib.core

import com.gitlab.kordlib.cache.api.data.description
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
        var mentionable: Boolean? = null,
        @SerialName("guild_id")
        var guildId: String? = null
) {
    companion object {
        val description get() = description(RoleData::id)

        fun from(entity: Role) = with(entity) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable) }
        fun from(entity: DeletedGuildRole) = with(entity) { RoleData(id, guildId) }
        fun from(entity: AuditLogRoleChange) = with(entity) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable) }
        fun from(entity: GuildRole) = with(entity.role) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable, entity.guildId) }
    }
}


@Serializable
data class ChannelData(
        val id: String,
        var type: ChannelType,
        @SerialName("guild_id")
        var guildId: String? = null,
        var position: Int? = null,
        @SerialName("permission_overwrites")
        var permissionOverwrites: List<Overwrite>? = null,
        var name: String? = null,
        var topic: String? = null,
        var nsfw: Boolean? = null,
        @SerialName("last_message_id")
        var lastMessageId: String? = null,
        var bitrate: Int? = null,
        @SerialName("user_limit")
        var userLimit: Int? = null,
        @SerialName("rate_limit_per_user")
        var rateLimitPerUser: Int? = null,
        var recipients: List<String>? = null,
        var icon: String? = null,
        @SerialName("owner_id")
        var ownerId: String? = null,
        @SerialName("application_id")
        var applicationId: String? = null,
        @SerialName("parent_id")
        var parentId: String? = null,
        @SerialName("last_pin_timestamp")
        var lastPinTimestamp: String? = null
) {
    companion object {
        val description get() = description(ChannelData::id)
    }

    fun from(entity: Channel) = with(entity) {
        ChannelData(
                id,
                type,
                guildId,
                position,
                permissionOverwrites,
                name,
                topic,
                nsfw,
                lastMessageId,
                bitrate,
                userLimit,
                rateLimitPerUser,
                recipients?.map { it.id },
                icon,
                ownerId,
                applicationId,
                parentId,
                lastPinTimestamp
        )
    }
}


@Serializable
data class GuildData(
        val id: String,
        var name: String? = null,
        var icon: String? = null,
        var splash: String? = null,
        var owner: Boolean? = null,
        @SerialName("owner_id")
        var ownerId: String? = null,
        var permissions: Permissions? = null,
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
        var banner: String? = null,
        @SerialName("premium_tier")
        var premiumTier: PremiumTier? = null,
        @SerialName("premium_subscription_count")
        var premiumSubscriptionCount: Int? = null,
        @SerialName("preferred_locale")
        var preferredLocale: String? = null
) {
    companion object {

        val description = description(GuildData::id) {
            link(GuildData::id to RoleData::guildId)
            link(GuildData::id to ChannelData::guildId)
            link(GuildData::id to GuildMemberData::guildId)
            link(GuildData::id to MessageData::guildId)
            link(GuildData::id to WebhookData::guildId)
        }

        fun from(entity: PartialGuild) = with(entity) { GuildData(id, name, icon, owner = owner, permissions = permissions) }
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
                    banner,
                    premiumTier,
                    premiumSubscriptionCount,
                    preferredLocale
            )
        }

    }
}

private val GuildMemberData.id get() = "$userId$guildId"

@Serializable
data class GuildMemberData(
        var userId: String,
        @SerialName("guild_id")
        var guildId: String,
        var nick: String? = null,
        var roles: List<String>,
        @SerialName("joined_at")
        var joinedAt: String,
        var deaf: Boolean,
        var mute: Boolean
) {
    companion object {
        val description get() = description(GuildMemberData::id)

        fun from(userId: String, guildId: String, entity: GuildMember) =
                with(entity) { GuildMemberData(userId, guildId, nick, roles, joinedAt, deaf, mute) }

        fun from(userId: String, entity: AddedGuildMember) =
                with(entity) { GuildMemberData(userId, guildId, nick, roles, joinedAt, deaf, mute) }

        fun from(userId: String, guildId: String, entity: PartialGuildMember) =
                with(entity) { GuildMemberData(userId, guildId, nick, roles, joinedAt, deaf, mute) }

    }
}


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
) {
    companion object {

        val description
            get() = description(UserData::id) {
                link(UserData::id to GuildMemberData::userId)
                link(UserData::id to MessageData::authorId)
                link(UserData::id to WebhookData::userid)
            }

        fun from(entity: User) = with(entity) {
            UserData(
                    id,
                    username,
                    discriminator,
                    avatar,
                    bot,
                    mfaEnable,
                    locale,
                    flags,
                    premiumType,
                    verified,
                    email
            )
        }

    }
}


@Serializable
data class MessageData(
        val id: String,
        @SerialName("channel_id")
        var channelId: String,
        @SerialName("guild_id")
        var guildId: String? = null,
        var authorId: String? = null,
        var content: String? = null,
        var timestamp: String? = null,
        @SerialName("edited_timestamp")
        var editedTimestamp: String? = null,
        var tts: Boolean? = null,
        @SerialName("mention_everyone")
        var mentionEveryone: Boolean? = null,
        var mentions: List<String>? = null,
        @SerialName("mention_roles")
        var mentionRoles: List<String>? = null,
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
) {
    companion object {
        val description get() = description(MessageData::id)

        fun from(entity: Message) = with(entity) {
            MessageData(
                    id,
                    channelId,
                    guildId,
                    author.id,
                    content,
                    timestamp,
                    editedTimestamp,
                    tts,
                    mentionEveryone,
                    mentions.map { it.id },
                    mentionRoles.map { it.id },
                    attachments,
                    embeds,
                    reactions,
                    nonce,
                    pinned,
                    webhookId,
                    type,
                    activity,
                    application
            )
        }
    }
}

@Serializable
data class EmojiData(
        val id: String,
        var name: String,
        var roles: List<String>? = null,
        var user: User? = null,
        @SerialName("require_colons")
        var requireColons: Boolean? = null,
        var managed: Boolean? = null,
        var animated: Boolean? = null
) {
    companion object {
        val description get() = description(EmojiData::id)

        fun from(id: String, entity: Emoji) =
                with(entity) { EmojiData(id, name, roles, user, requireColons, managed, animated) }

        fun from(entity: PartialEmoji) =
                with(entity) { EmojiData(id, name) }

    }
}


@Serializable
data class WebhookData(
        val id: String,
        @SerialName("guild_id")
        var guildId: String? = null,
        var channelId: String,
        var userid: String? = null,
        var name: String? = null,
        var avatar: String? = null,
        var token: String
) {
    companion object {
        val description get() = description(WebhookData::id)

        fun from(entity: Webhook) = with(entity) { WebhookData(id, guildId, channelId, user?.id, name, avatar, token) }
    }
}