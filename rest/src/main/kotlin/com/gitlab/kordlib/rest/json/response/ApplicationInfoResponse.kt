@file:Suppress("ArrayInDataClass")

package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationInfoResponse(
        val id: String,
        val name: String,
        val icon: String? = null,
        val description: String? = null,
        @SerialName("rpc_origins")
        val rpcOrigins: Array<String>? = null,
        @SerialName("bot_public")
        val botPublic: Boolean,
        @SerialName("bot_require_code_grant")
        val botRequireCodeGrant: Boolean,
        val owner: User
)