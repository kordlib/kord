package dev.kord.common.serialization

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/** Serializer that encodes and decodes [Instant]s in [epoch milliseconds][Instant.toEpochMilliseconds]. */
public object InstantInEpochMillisecondsSerializer : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.kord.common.serialization.InstantInEpochMilliseconds", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.toEpochMilliseconds())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochMilliseconds(decoder.decodeLong())
    }
}


/** Serializer that encodes and decodes [Instant]s in [epoch seconds][Instant.epochSeconds]. */
public object InstantInEpochSecondsSerializer : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.kord.common.serialization.InstantInEpochSeconds", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.epochSeconds)
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochSeconds(decoder.decodeLong())
    }
}
