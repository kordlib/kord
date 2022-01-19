package dev.kord.rest.json.response

import dev.kord.rest.json.JsonErrorCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Represents a [Discord error response](https://discord.com/developers/docs/topics/opcodes-and-status-codes#json).
 */
@Serializable
data class DiscordErrorResponse(
    val code: JsonErrorCode = JsonErrorCode.Unknown,
    val errors: JsonElement? = null,
    val message: String? = null,
)
