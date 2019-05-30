package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuildMember(
        val user: User? = null,
        val nick: String? = null,
        val roles: List<String>,
        @SerialName("joined_at")
        val joinedAt: String,
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
        val nick: String
)

@Serializable
data class PartialGuildMember(
        val nick: String,
        val roles: List<String>,
        @SerialName("joined_at")
        val joinedAt: String,
        val deaf: Boolean,
        val mute: Boolean
)

@Serializable
data class OptionallyMemberUser(
        val id: String,
        val username: String,
        val discriminator: String,
        val avatar: String? = null,
        val bot: Boolean? = null,
        @SerialName("mfa_enable")
        val mfaEnable: Boolean? = null,
        val locale: String? = null,
        val flags: Int? = null,
        @SerialName("premium_type")
        val premiumType: Int? = null,
        val member: PartialGuildMember
)