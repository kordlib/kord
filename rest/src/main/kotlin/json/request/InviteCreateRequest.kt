package dev.kord.rest.json.request

import dev.kord.common.entity.TargetUserType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
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
