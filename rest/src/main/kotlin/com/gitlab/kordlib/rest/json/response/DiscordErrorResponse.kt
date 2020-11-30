package dev.kord.rest.json.response

import dev.kord.rest.json.JsonErrorCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a [Discord error response](https://discord.com/developers/docs/topics/opcodes-and-status-codes#json).
 */
@Serializable
class DiscordErrorResponse(
        val code: JsonErrorCode = JsonErrorCode.Unknown,
        val errors: Map<String, DiscordFieldError> = emptyMap(),
        val message: String = "",
)

/**
 * An error for a specific field.
 */
@Serializable
class DiscordFieldError(
        @SerialName("_errors")
        val errors: List<DiscordErrorDetail>,
)

/**
 * The detailed code and message for a [DiscordFieldError].
 */
@Serializable
class DiscordErrorDetail(
        val code: String,
        val message: String,
)
