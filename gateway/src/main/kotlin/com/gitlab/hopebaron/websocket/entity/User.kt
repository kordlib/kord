package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
        val id: String,
        val username: String,
        val discriminator: String,
        val avatar: String?,
        val bot: Boolean?,
        @SerialName("mfa_enable")
        val mfaEnable: Boolean?,
        val locale: String?,
        val flags: Int?,
        @SerialName("premium_type")
        val premiumType: Int?,
        val verified: Boolean?,
        val email: String?
)