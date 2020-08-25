package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class DMCreateRequest(
        @SerialName("recipient_id")
        val userId: String
)

@Serializable
@KordUnstableApi
data class GroupDMCreateRequest(
        @SerialName("access_tokens")
        val tokens: List<String>,
        val nick: Map<String, String>)

@Serializable
@KordUnstableApi
data class CurrentUserModifyRequest(
        val username: String? = null,
        val avatar: String? = null
)

@Serializable
@KordUnstableApi
data class UserAddDMRequest(
        @SerialName("access_token")
        val token: String,
        val nick: String
)