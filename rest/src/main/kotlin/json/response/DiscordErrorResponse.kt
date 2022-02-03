package dev.kord.rest.json.response

import dev.kord.rest.json.JsonErrorCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Represents a [Discord error response](https://discord.com/developers/docs/topics/opcodes-and-status-codes#json).
 */
@Serializable
public data class DiscordErrorResponse(
    val code: JsonErrorCode = JsonErrorCode.Unknown,
    val errors: JsonElement? = null,
    val message: String? = null,
)
