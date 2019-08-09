package com.gitlab.kordlib.rest.json.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InviteCreatePostRequest(
        @SerialName("max_age")
        val age: Int = 86400,
        @SerialName("max_uses")
        val uses: Int = 0,
        val temporary: Boolean = false,
        val unique: Boolean = false
)