package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DMCreateRequest(
    @SerialName("recipient_id")
    val userId: Snowflake,
)

@Serializable
public data class GroupDMCreateRequest(
    @SerialName("access_tokens")
    val tokens: List<String>,
    val nick: Map<String, String>
)

@Serializable
public data class CurrentUserModifyRequest(
    val username: Optional<String> = Optional.Missing(),
    val avatar: Optional<String?> = Optional.Missing()
)

@Serializable
public data class UserAddDMRequest(
    @SerialName("access_token")
    val token: String,
    val nick: String
)
