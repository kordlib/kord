package dev.kord.rest.json.response

import dev.kord.common.entity.DiscordUser
import kotlinx.serialization.Serializable

@Serializable
public data class AnswerVotersGetResponse(val users: List<DiscordUser>)
