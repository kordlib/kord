package dev.kord.rest.json.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object NothingSerializer : KSerializer<Nothing> {
    override val descriptor: SerialDescriptor
        get() = throw SerializationException("This type can no longer be serialized.")

    override fun deserialize(decoder: Decoder): Nothing {
        throw SerializationException("This type can no longer be serialized.")
    }

    override fun serialize(encoder: Encoder, value: Nothing) {
        throw SerializationException("This type can no longer be serialized.")
    }
}