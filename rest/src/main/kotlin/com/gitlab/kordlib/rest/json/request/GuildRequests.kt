package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateGuildRequest(
        val name: String,
        val region: String,
        val icon: String? = null,
        @SerialName("verification_level")
        val verificationLevel: VerificationLevel,
        @SerialName("default_message_notifications")
        val defaultNotificationLevel: DefaultMessageNotificationLevel,
        val explicitContentFilter: ExplicitContentFilter,
        val roles: List<Role> = emptyList(),
        val channels: List<com.gitlab.kordlib.rest.json.request.CreateGuildChannelRequest> = emptyList()
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
        val nick: String? = null,
        val roles: List<String>? = null,
        val mute: Boolean? = null,
        val deaf: Boolean? = null
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
        val name: String? = null,
        val permissions: Permissions? = null,
        val color: Int = 0,
        @SerialName("hoist")
        val separate: Boolean = false,
        val mentionable: Boolean = false
)

@Serializable
data class ModifyGuildRolePositionRequest(
        val id: String,
        val position: Int
)

@Serializable
data class ModifyGuildRoleRequest(
        val name: String? = null,
        val permissions: Permissions? = null,
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
        val name: String? = null,
        val region: String? = null,
        @SerialName("verification_level")
        val verificationLevel: VerificationLevel? = null,
        @SerialName("default_message_notifications")
        val defaultMessageNotificationLevel: DefaultMessageNotificationLevel? = null,
        @SerialName("explicit_content_filter")
        val contentFilter: ExplicitContentFilter? = null,
        @SerialName("afk_channel_id")
        val afkChannel: String? = null,
        @SerialName("afk_timeout")
        val afkTimeout: Int? = null,
        val icon: String? = null,
        @SerialName("owner_id")
        val ownerId: String? = null,
        val spalsh: String? = null,
        @SerialName("system_channel_id")
        val systemChannelId: String? = null
)