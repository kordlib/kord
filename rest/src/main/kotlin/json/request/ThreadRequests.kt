package dev.kord.rest.json.request

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThreadModifyPatchRequest(
    val name: Optional<String> = Optional.Missing(),
    val archived: OptionalBoolean = OptionalBoolean.Missing,
    val autoArchiveDuration: OptionalInt = OptionalInt.Missing,
    val locked: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("rate_limit_per_user")
    val ratelimitPerUser: OptionalInt = OptionalInt.Missing
)