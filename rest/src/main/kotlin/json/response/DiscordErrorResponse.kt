package dev.kord.rest.json.response

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.optional
import dev.kord.rest.json.JsonErrorCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

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
@Serializable(DiscordFieldError.Serializer::class)
class DiscordFieldError(
    @SerialName("_errors")
    val errors: List<DiscordErrorDetail> = emptyList(),
    val nestedErrors: Optional<JsonObject> = Optional.Missing()
) {
    internal object Serializer : KSerializer<DiscordFieldError> {

        override val descriptor: SerialDescriptor
            get() = JsonObject.serializer().descriptor


        override fun deserialize(decoder: Decoder): DiscordFieldError {
            decoder as JsonDecoder
            val json = decoder.decodeJsonElement().jsonObject
            // Direct Error message
            if (json.containsKey("_errors"))
                return DiscordFieldError(
                    Json.decodeFromJsonElement(ListSerializer(DiscordErrorDetail.serializer()), json["_errors"]!!)
                )

            // Nested fields have no definite structure.
            return DiscordFieldError(nestedErrors = json.optional())
        }

        override fun serialize(encoder: Encoder, value: DiscordFieldError) {
            TODO("Not yet implemented")
        }

    }
}

/**
 * The detailed code and message for a [DiscordFieldError].
 */
@Serializable
class DiscordErrorDetail(
    val code: String,
    val message: String,
)
