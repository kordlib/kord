package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListClassDesc
import kotlinx.serialization.internal.ArrayListSerializer

@Serializable
data class GuildCreatePostRequest(
        val name: String,
        val region: String,
        val icon: String? = null,
        @SerialName("verification_level")
        val verificationLevel: VerificationLevel,
        @SerialName("default_message_notifications")
        val defaultNotificationLevel: DefaultMessageNotificationLevel,
        val explicitContentFilter: ExplicitContentFilter,
        val roles: List<Role> = emptyList(),
        val channels: List<GuildChannelCreatePostRequest> = emptyList()
)

@Serializable
data class GuildChannelCreatePostRequest(
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

data class ModifyGuildChannelPositionRequest(val swaps: List<Pair<String, Int>>) {

    companion object Serializer : SerializationStrategy<ModifyGuildChannelPositionRequest> {
        override val descriptor: SerialDescriptor
            get() = ArrayListClassDesc(ChannelPosition.serializer().descriptor)

        override fun serialize(encoder: Encoder, obj: ModifyGuildChannelPositionRequest) {
            val positions = obj.swaps.map { ChannelPosition(it.first, it.second) }
            ArrayListSerializer(ChannelPosition.serializer()).serialize(encoder, positions)
        }

    }

    @Serializable
    private data class ChannelPosition(val id: String, val position: Int)
}

@Serializable
data class GuildMemberAddPutRequest(
        @SerialName("access_token") val token: String,
        val nick: String? = null,
        val roles: List<String>? = null,
        val mute: Boolean? = null,
        val deaf: Boolean? = null
)

@Serializable
data class GuildMemberModifyPatchRequest(
        val nick: String? = null,
        val roles: List<String>? = null,
        val mute: Boolean? = null,
        val deaf: Boolean? = null,
        @SerialName("channel_id")
        val channelId: String? = null
)


@Serializable
data class GuildBanAddPutRequest(
        val reason: String? = null,
        @SerialName("delete-message-days")
        val deleteMessagesDays: Int? = null
)

@Serializable
data class GuildRoleCreatePostRequest(
        val name: String? = null,
        val permissions: Permissions? = null,
        val color: Int = 0,
        @SerialName("hoist")
        val separate: Boolean = false,
        val mentionable: Boolean = false
)

data class ModifyGuildRolePositionRequest(val swaps: List<Pair<String, Int>>) {

    companion object Serializer : SerializationStrategy<ModifyGuildRolePositionRequest> {
        override val descriptor: SerialDescriptor
            get() = ArrayListClassDesc(RolePosition.serializer().descriptor)

        override fun serialize(encoder: Encoder, obj: ModifyGuildChannelPositionRequest) {
            val positions = obj.swaps.map { RolePosition(it.first, it.second) }
            ArrayListSerializer(RolePosition.serializer()).serialize(encoder, positions)
        }

    }

    @Serializable
    private data class RolePosition(val id: String, val position: Int)
}

@Serializable
data class GuildRoleModifyPatchRequest(
        val name: String? = null,
        val permissions: Permissions? = null,
        val color: Int? = null,
        @SerialName("hoist")
        val separate: Boolean? = null,
        val Mentionable: Boolean? = null
)

@Serializable
data class GuildIntegrationCreatePostRequest(val type: Int, val id: String)

@Serializable
data class GuildIntegrationModifyPatchRequest(
        @SerialName("expire_behavior")
        val expireBehavior: Int? = null,
        @SerialName("expire_grace_period")
        val expirePeriod: Int? = null,
        @SerialName("enable_emoticons")
        val emoticons: Boolean? = null
)

@Serializable
data class GuildEmbedModifyPatchRequest(
        val enabled: Boolean,
        @SerialName("channel_id")
        val channelId: String
)

@Serializable
data class CurrentUserNicknameModifyPatchRequest(val nick: String? = null)

@Serializable
data class GuildModifyPatchRequest(
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