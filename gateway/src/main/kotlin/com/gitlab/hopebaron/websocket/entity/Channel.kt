package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Channel(
        val id: String,
        val type: Int,
        @SerialName("guild_id")
        val guildId: String? = null,
        val position: Int? = null,
        @SerialName("permission_overwrites")
        val permissionOverwrites: List<Overwrite>? = null,
        val name: String? = null,
        val topic: String? = null,
        val nsfw: Boolean? = null,
        @SerialName("last_message_id")
        val lastMessageId: String? = null,
        val bitrate: Int? = null,
        @SerialName("user_limit")
        val userLimit: Int? = null,
        @SerialName("rate_limit_per_user")
        val rateLimitPerUser: Int? = null,
        val recipients: List<User>? = null,
        val icon: String? = null,
        @SerialName("owner_id")
        val ownerId: String? = null,
        @SerialName("application_id")
        val applicationId: String? = null,
        @SerialName("parent_id")
        val parentId: String? = null,
        @SerialName("last_pin_timestamp")
        val lastPinTimestamp: String? = null
)