package com.gitlab.hopebaron.rest.json.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddDMUserRequest(
        @SerialName("access_token")
        val accessToken: String,
        val nick: String)