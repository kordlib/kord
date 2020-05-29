package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.TargetUserType
import kotlinx.serialization.*

@Serializable
data class InviteCreateRequest(
        @SerialName("max_age")
        val age: Int? = null,
        @SerialName("max_uses")
        val uses: Int? = null,
        val temporary: Boolean? = null,
        val unique: Boolean? = null,
        @SerialName("target_user")
        val targetUser: String? = null,
        @SerialName("target_user_type")
        val targetUserType: TargetUserType? = null
)
