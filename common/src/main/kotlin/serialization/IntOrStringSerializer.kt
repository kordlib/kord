package dev.kord.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int

internal object IntOrStringSerializer : KSerializer<String> {
    private val backingSerializer = JsonPrimitive.serializer()

    /*
     * Delegating serializers should not reuse descriptors:
     * https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#delegating-serializers
     *
     * however `SerialDescriptor("...", backingSerializer.descriptor)` will throw since
     * `JsonPrimitive.serializer().kind` is `PrimitiveKind.STRING` (`SerialDescriptor()` does not allow
     * `PrimitiveKind`)
     * -> use `PrimitiveSerialDescriptor("...", PrimitiveKind.STRING)` instead
     */
    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "dev.kord.common.serialization.IntOrString",
        PrimitiveKind.STRING,
    )

    override fun serialize(encoder: Encoder, value: String) {
        val jsonPrimitive = value.toIntOrNull()?.let { JsonPrimitive(it) } ?: JsonPrimitive(value)
        encoder.encodeSerializableValue(backingSerializer, jsonPrimitive)
    }

    override fun deserialize(decoder: Decoder): String {
        val jsonPrimitive = decoder.decodeSerializableValue(backingSerializer)
        return if (jsonPrimitive.isString) jsonPrimitive.content else jsonPrimitive.int.toString()
    }
}