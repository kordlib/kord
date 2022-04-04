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
) {
    @Deprecated("'age' was renamed to 'maxAge'", ReplaceWith("this.maxAge"))
    public val age: OptionalInt
        get() = maxAge.value?.inWholeSeconds?.toInt()?.optionalInt() ?: OptionalInt.Missing

    @Deprecated("'uses' was renamed to 'maxUses'", ReplaceWith("this.maxUses"))
    public val uses: OptionalInt
        get() = maxUses
}
