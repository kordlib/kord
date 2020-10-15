package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.rest.json.JsonErrorCode
import kotlinx.serialization.Serializable

/**
 * Represents a [Discord error response](https://discord.com/developers/docs/topics/opcodes-and-status-codes#json).
 */
@Serializable
class DiscordErrorResponse(val code: JsonErrorCode = JsonErrorCode.Unknown, val message: String = "")