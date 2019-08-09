package com.gitlab.kordlib.rest.json.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DMCreatePostRequest(
        @SerialName("recipient_id")
        val userId: String
)

@Serializable
data class GroupDMCreatePostRequest(
        @SerialName("access_tokens")
        val tokens: List<String>,
        val nick: Map<String, String>)

@Serializable
data class CurrentUserModifyPatchRequest(
        val username: String? = null,
        val avatar: String? = null
)

@Serializable
data class UserAddDMPutRequest(
        @SerialName("access_token")
        val token: String,
        val nick: String
)