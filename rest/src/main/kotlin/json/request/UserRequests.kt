package dev.kord.rest.json.request

import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DMCreateRequest(
        @SerialName("recipient_id")
        val userId: String
)

@Serializable
data class GroupDMCreateRequest(
        @SerialName("access_tokens")
        val tokens: List<String>,
        val nick: Map<String, String>)

@Serializable
data class CurrentUserModifyRequest(
        val username: Optional<String> = Optional.Missing(),
        val avatar: Optional<String?> = Optional.Missing()
)

@Serializable
data class UserAddDMRequest(
        @SerialName("access_token")
        val token: String,
        val nick: String
)