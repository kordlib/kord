package dev.kord.rest.json.response

import dev.kord.common.entity.MFALevel
import kotlinx.serialization.Serializable

@Serializable
public data class GuildMFALevelModifyResponse(val level: MFALevel)
