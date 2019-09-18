package com.gitlab.kordlib.rest.json.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DMCreateRequest(
        @SerialName("recipient_id")
        val userId: String
)

@Serializable
data class GroupDMCreateRequest(
        @SerialName("access_tokens")
        val tokens: List<String>,
        val nick: Map<String, String>)

@Serializable
data class CurrentUserModifyRequest(
        val username: String? = null,
        val avatar: String? = null
)

@Serializable
data class UserAddDMRequest(
        @SerialName("access_token")
        val token: String,
        val nick: String
)