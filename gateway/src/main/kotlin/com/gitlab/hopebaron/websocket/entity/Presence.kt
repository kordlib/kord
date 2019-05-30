package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresenceUpdateData(
        val user: PresenceUser,
        val roles: List<String>?,
        val game: Activity?,
        @SerialName("guild_id")
        val guildId: String?,
        val status: String,
        val activities: List<Activity>,
        @SerialName("client_status")
        val clientStatus: ClientStatus
)

@Serializable
data class PresenceUser(
        val id: String,
        val username: String?,
        val discriminator: String?,
        val avatar: String?,
        val bot: String?,
        @SerialName("mfa_enable")
        val mfaEnable: String?,
        val locale: String?,
        val flags: String?,
        @SerialName("premium_type")
        val premiumType: String?,
        val verified: String?,
        val email: String?
)

@Serializable
data class ClientStatus(val desktop: String?, val mobile: String?, val web: String?)
