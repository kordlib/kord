package com.gitlab.kordlib.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuildMember(
        val user: User? = null,
        val nick: String? = null,
        val roles: List<String>,
        @SerialName("joined_at")
        val joinedAt: String,
        @SerialName("premium_since")
        val premiumSince: String? = null,
        val deaf: Boolean,
        val mute: Boolean
)

@Serializable
data class AddedGuildMember(
        val user: User? = null,
        val nick: String? = null,
        val roles: List<String>,
        @SerialName("joined_at")
        val joinedAt: String,
        @SerialName("premium_since")
        val premiumSince: String? = null,
        val deaf: Boolean,
        val mute: Boolean,
        val guildId: String
)

@Serializable
data class RemovedGuildMember(
        @SerialName("guild_id")
        val guildId: String,
        val user: User
)

@Serializable
data class UpdatedGuildMember(
        @SerialName("guild_id")
        val guildId: String,
        val roles: List<String>,
        val user: User,
        val nick: String? = null
)

@Serializable
data class PartialGuildMember(
        val nick: String? = null,
        val roles: List<String>,
        @SerialName("joined_at")
        val joinedAt: String,
        val deaf: Boolean,
        val mute: Boolean
)
