package com.gitlab.hopebaron.rest.json.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateDMRequest(@SerialName("recipient_id") val userId: String)

@Serializable
data class CreateGroupDMRequest(
        @SerialName("access_tokens")
        val tokens: List<String>,
        val nick: Map<String, String>)

@Serializable
data class ModifyCurrentUserRequest(val username: String? = null,
                                    val avatar: String? = null)

@Serializable
data class AddDMUserRequest(
        @SerialName("access_token")
        val token: String,
        val nick: String)