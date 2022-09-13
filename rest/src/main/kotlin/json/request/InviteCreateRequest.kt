package dev.kord.rest.json.request

import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.optional.*
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.DeprecationLevel.HIDDEN

@Serializable
public data class InviteCreateRequest(
    @SerialName("max_age")
    val maxAge: Optional<DurationInSeconds> = Optional.Missing(),
    @SerialName("max_uses")
    val maxUses: OptionalInt = OptionalInt.Missing,
    val temporary: OptionalBoolean = OptionalBoolean.Missing,
    val unique: OptionalBoolean = OptionalBoolean.Missing,
    /** @suppress */
    @Deprecated("This is no longer documented. Use 'targetUserId' instead.", ReplaceWith("this.targetUserId"), level = HIDDEN)
    @SerialName("target_user")
    val targetUser: OptionalSnowflake = OptionalSnowflake.Missing,
    /** @suppress */
    @Deprecated("This is no longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"), level = HIDDEN)
    @SerialName("target_user_type")
    val targetUserType: Optional<@Suppress("DEPRECATION_ERROR") dev.kord.common.entity.TargetUserType> = Optional.Missing(),
    @SerialName("target_type")
    val targetType: Optional<InviteTargetType> = Optional.Missing(),
    @SerialName("target_user_id")
    val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("target_application_id")
    val targetApplicationId: OptionalSnowflake = OptionalSnowflake.Missing,
) {
    /** @suppress */
    @Deprecated("'age' was renamed to 'maxAge'", ReplaceWith("this.maxAge"), level = HIDDEN)
    public val age: OptionalInt
        get() = maxAge.value?.inWholeSeconds?.toInt()?.optionalInt() ?: OptionalInt.Missing

    /** @suppress */
    @Deprecated("'uses' was renamed to 'maxUses'", ReplaceWith("this.maxUses"), level = HIDDEN)
    public val uses: OptionalInt
        get() = maxUses
}
