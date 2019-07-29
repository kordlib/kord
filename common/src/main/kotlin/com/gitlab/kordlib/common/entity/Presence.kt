package com.gitlab.kordlib.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresenceUpdateData(
        val user: PresenceUser,
        val roles: List<String>? = null,
        val game: Activity? = null,
        @SerialName("guild_id")
        val guildId: String? = null,
        val status: String,
        val activities: List<Activity>,
        @SerialName("client_status")
        val clientStatus: ClientStatus
)

@Serializable
data class PresenceUser(
        val id: String,
        val username: String? = null,
        val discriminator: String? = null,
        val avatar: String? = null,
        val bot: String? = null,
        @SerialName("mfa_enable")
        val mfaEnable: String? = null,
        val locale: String? = null,
        val flags: String? = null,
        @SerialName("premium_type")
        val premiumType: String? = null,
        val verified: String? = null,
        val email: String? = null
)

@Serializable
data class ClientStatus(val desktop: String? = null, val mobile: String? = null, val web: String? = null)
