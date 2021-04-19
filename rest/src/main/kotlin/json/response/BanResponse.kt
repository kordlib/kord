package dev.kord.rest.json.response

import dev.kord.common.entity.DiscordUser
import kotlinx.serialization.Serializable

@Serializable
data class BanResponse(
    val reason: String?,
    val user: DiscordUser
)