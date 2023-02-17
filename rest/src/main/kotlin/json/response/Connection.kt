package dev.kord.rest.json.response

import dev.kord.common.entity.DiscordGuildIntegration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Connection(
    val id: String,
    val name: String,
    val type: String,
    val revoked: Boolean,
    val integrations: List<DiscordGuildIntegration>,
    val verified: Boolean,
    @SerialName("friend_sync")
    val friendSync: Boolean,
    @SerialName("show_activity")
    val showActivity: Boolean,
    val visibility: Int
)
//TODO add a visibility enum class