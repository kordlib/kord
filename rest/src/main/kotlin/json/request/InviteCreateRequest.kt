package dev.kord.rest.json.request

import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class InviteCreateRequest(
    @SerialName("max_age")
    val age: OptionalInt = OptionalInt.Missing,
    @SerialName("max_uses")
    val uses: OptionalInt = OptionalInt.Missing,
    val temporary: OptionalBoolean = OptionalBoolean.Missing,
    val unique: OptionalBoolean = OptionalBoolean.Missing,
    @Deprecated("This is no longer documented. Use 'targetUserId' instead.", ReplaceWith("this.targetUserId"))
    @SerialName("target_user")
    val targetUser: OptionalSnowflake = OptionalSnowflake.Missing,
    @Deprecated("This is no longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"))
    @SerialName("target_user_type")
    val targetUserType: Optional<@Suppress("DEPRECATION") dev.kord.common.entity.TargetUserType> = Optional.Missing(),
    @SerialName("target_type")
    val targetType: Optional<InviteTargetType> = Optional.Missing(),
    @SerialName("target_user_id")
    val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("target_application_id")
    val targetApplicationId: OptionalSnowflake = OptionalSnowflake.Missing,
)
