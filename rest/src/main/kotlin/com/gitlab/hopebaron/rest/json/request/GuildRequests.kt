package com.gitlab.hopebaron.rest.json.request

import com.gitlab.hopebaron.common.entity.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateGuildRequest(
        val name: String,
        val region: String,
        @SerialName("verification_level")
        val verificationLevel: VerificationLevel,
        @SerialName("default_message_notifications")
        val defaultNotificationLevel: DefaultMessageNotificationLevel
)

@Serializable
data class CreateGuildChannelRequest(
        val name: String,
        val type: ChannelType? = null,
        val topic: String? = null,
        val bitrate: Int? = null,
        @SerialName("user_limit")
        val userLimit: Int? = null,
        @SerialName("rate_limit_per_user")
        val rateLimitPerUser: Int? = null,
        val position: Int? = null,
        @SerialName("permission_overwrites")
        val permissionOverwrite: List<Overwrite>? = null,
        @SerialName("parent_id")
        val parentId: String? = null,
        val nsfw: Boolean? = null
)

@Serializable
data class ModifyGuildChannelPositionRequest(val id: String, val position: Int)

@Serializable
data class AddGuildMemberRequest(
        @SerialName("access_token") val token: String,
        val nick: String,
        val roles: List<String>,
        val mute: Boolean,
        val deaf: Boolean
)

@Serializable
data class ModifyGuildMemberRequest(
        val nick: String? = null,
        val roles: List<String>? = null,
        val mute: Boolean? = null,
        val deaf: Boolean? = null,
        @SerialName("channel_id")
        val channelId: String? = null
)


@Serializable
data class AddGuildBanRequest(
        val reason: String? = null,
        @SerialName("delete-message-days")
        val deleteMessagesDays: String? = null
)

@Serializable
data class CreateGuildRoleRequest(
        val name: String,
        val permissions: Permission,
        val color: Int,
        @SerialName("hoist")
        val separate: Boolean,
        val Mentionable: Boolean
)

@Serializable
data class ModifyGuildRolePositionRequest(
        val id: String,
        val position: String
)

@Serializable
data class ModifyGuildRoleRequest(
        val name: String? = null,
        val permissions: Permission? = null,
        val color: Int? = null,
        @SerialName("hoist")
        val separate: Boolean? = null,
        val Mentionable: Boolean? = null
)

@Serializable
data class CreateGuildIntegrationRequest(val type: Int, val id: String)

@Serializable
data class ModifyGuildIntegrationRequest(
        @SerialName("expire_behavior")
        val expireBehavior: Int? = null,
        @SerialName("expire_grace_period")
        val expirePeriod: Int? = null,
        @SerialName("enable_emoticons")
        val emoticons: Boolean? = null
)

@Serializable
data class ModifyGuildEmbedRequest(
        val enabled: Boolean,
        @SerialName("channel_id")
        val channelId: String
)

@Serializable
data class ModifyCurrentUserNicknameRequest(val nick: String? = null)

@Serializable
data class ModifyGuildRequest(
        val name: String,
        val region: String,
        @SerialName("verification_level")
        val verificationLevel: VerificationLevel,
        @SerialName("default_message_notifications")
        val defaultMessageNotificationLevel: DefaultMessageNotificationLevel,
        @SerialName("explicit_content_filter")
        val contentFilter: ExplicitContentFilter,
        @SerialName("afk_channel_id")
        val afkChannel: String,
        @SerialName("afk_timeout")
        val afkTimeout: Int,
        val icon: String,
        @SerialName("owner_id")
        val ownerId: String,
        val spalsh: String,
        @SerialName("system_channel_id")
        val systemChannelId: String
)