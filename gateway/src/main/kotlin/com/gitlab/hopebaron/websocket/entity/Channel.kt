package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Channel(
        val id: String,
        val type: Int,
        @SerialName("guild_id")
        val guildId: String?,
        val position: Int?,
        @SerialName("permission_overwrites")
        val permissionOverwrites: List<Overwrite>?,
        val name: String?,
        val topic: String?,
        val nsfw: Boolean?,
        @SerialName("last_message_id")
        val lastMessageId: String?,
        val bitrate: Int?,
        @SerialName("user_limit")
        val userLimit: Int?,
        @SerialName("rate_limit_per_user")
        val rateLimitPerUser: Int?,
        val recipients: List<User>?,
        val icon: String?,
        @SerialName("owner_id")
        val ownerId: String?,
        @SerialName("application_id")
        val applicationId: String?,
        @SerialName("parent_id")
        val parentId: String?,
        @SerialName("last_pin_timestamp")
        val lastPinTimestamp: String?
)