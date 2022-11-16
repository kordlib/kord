package dev.kord.rest.json.request

import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.optional.*
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class InviteCreateRequest(
    @SerialName("max_age")
    val maxAge: Optional<DurationInSeconds> = Optional.Missing(),
    @SerialName("max_uses")
    val maxUses: OptionalInt = OptionalInt.Missing,
    val temporary: OptionalBoolean = OptionalBoolean.Missing,
    val unique: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("target_type")
    val targetType: Optional<InviteTargetType> = Optional.Missing(),
    @SerialName("target_user_id")
    val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("target_application_id")
    val targetApplicationId: OptionalSnowflake = OptionalSnowflake.Missing,
)
