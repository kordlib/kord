package dev.kord.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.long

internal object LongOrStringSerializer : KSerializer<String> {
    /*
     * Delegating serializers should not reuse descriptors:
     * https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#delegating-serializers
     *
     * however `SerialDescriptor("...", JsonPrimitive.serializer().descriptor)` will throw since
     * `JsonPrimitive.serializer().descriptor.kind` is `PrimitiveKind.STRING` (`SerialDescriptor()` does not allow
     * `PrimitiveKind`)
     * -> use `PrimitiveSerialDescriptor("...", PrimitiveKind.STRING)` instead
     */
    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "dev.kord.common.serialization.LongOrString",
        PrimitiveKind.STRING,
    )

    override fun serialize(encoder: Encoder, value: String) {
        if (encoder is JsonEncoder) {
            val jsonPrimitive = value.toLongOrNull()?.let { JsonPrimitive(it) } ?: JsonPrimitive(value)
            encoder.encodeJsonElement(jsonPrimitive)
        } else {
            // fall back to a String for non-Json formats
            encoder.encodeString(value)
        }
    }

    override fun deserialize(decoder: Decoder): String =
        if (decoder is JsonDecoder) {
            val jsonPrimitive = decoder.decodeSerializableValue(JsonPrimitive.serializer())
            if (jsonPrimitive.isString) jsonPrimitive.content else jsonPrimitive.long.toString()
        } else {
            // fall back to a String for non-Json formats
            decoder.decodeString()
        }
}
