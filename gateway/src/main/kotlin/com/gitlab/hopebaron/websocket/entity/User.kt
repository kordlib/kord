package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
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
        val verified: Boolean? = null,
        val email: String? = null
)