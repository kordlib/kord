package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class DiscordGuildMember(
        val user: DiscordUser? = null,
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
@KordUnstableApi
data class DiscordAddedGuildMember(
        val user: DiscordUser? = null,
        val nick: String? = null,
        val roles: List<String>,
        @SerialName("joined_at")
        val joinedAt: String,
        @SerialName("premium_since")
        val premiumSince: String? = null,
        val deaf: Boolean,
        val mute: Boolean,
        @SerialName("guild_id")
        val guildId: String
)

@Serializable
@KordUnstableApi
data class DiscordRemovedGuildMember(
        @SerialName("guild_id")
        val guildId: String,
        val user: DiscordUser
)

@Serializable
@KordUnstableApi
data class DiscordUpdatedGuildMember(
        @SerialName("guild_id")
        val guildId: String,
        val roles: List<String>,
        val user: DiscordUser,
        val nick: String? = null,
        @SerialName("premium_since")
        val premiumSince: String? = null
)

@Serializable
@KordUnstableApi
data class DiscordPartialGuildMember(
        val nick: String? = null,
        val roles: List<String>,
        @SerialName("joined_at")
        val joinedAt: String,
        val deaf: Boolean,
        val mute: Boolean,
        @SerialName("premium_since")
        val premiumSince: String? = null
)
