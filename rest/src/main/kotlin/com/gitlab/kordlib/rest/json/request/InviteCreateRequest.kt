package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.TargetUserType
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalInt
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.*

@Serializable
data class InviteCreateRequest(
        @SerialName("max_age")
        val age: OptionalInt = OptionalInt.Missing,
        @SerialName("max_uses")
        val uses: OptionalInt = OptionalInt.Missing,
        val temporary: OptionalBoolean = OptionalBoolean.Missing,
        val unique: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("target_user")
        val targetUser: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("target_user_type")
        val targetUserType: Optional<TargetUserType> = Optional.Missing()
)
