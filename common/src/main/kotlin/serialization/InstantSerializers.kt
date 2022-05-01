package dev.kord.common.serialization

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


// epoch milliseconds

/** Serializer that encodes and decodes [Instant]s in [epoch milliseconds][Instant.toEpochMilliseconds]. */
public object InstantInEpochMillisecondsSerializer : KSerializer<Instant> {

    private val VALID_RANGE =
        Instant.fromEpochMilliseconds(Long.MIN_VALUE)..Instant.fromEpochMilliseconds(Long.MAX_VALUE)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.kord.common.serialization.InstantInEpochMilliseconds", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {

        if (value !in VALID_RANGE) throw SerializationException(
            "The Instant $value expressed as a number of milliseconds from the epoch Instant does not fit in the " +
                    "range of Long type and therefore cannot be serialized with InstantInEpochMillisecondsSerializer"
        )

        encoder.encodeLong(value.toEpochMilliseconds())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochMilliseconds(decoder.decodeLong())
    }
}

// TODO use this typealias instead of annotating types/properties with
//  @Serializable(with = InstantInEpochMillisecondsSerializer::class) once
//  https://github.com/Kotlin/kotlinx.serialization/issues/1895 is fixed
// /** An [Instant] that is [serializable][Serializable] with [InstantInEpochMillisecondsSerializer]. */
// public typealias InstantInEpochMilliseconds = @Serializable(with = InstantInEpochMillisecondsSerializer::class) Instant


// epoch seconds

/** Serializer that encodes and decodes [Instant]s in [epoch seconds][Instant.epochSeconds]. */
public object InstantInEpochSecondsSerializer : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.kord.common.serialization.InstantInEpochSeconds", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        // epochSeconds always fits in the range of Long type and never coerces -> no need for range check
        encoder.encodeLong(value.epochSeconds)
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochSeconds(decoder.decodeLong())
    }
}

// TODO use this typealias instead of annotating types/properties with
//  @Serializable(with = InstantInEpochSecondsSerializer::class) once
//  https://github.com/Kotlin/kotlinx.serialization/issues/1895 is fixed
// /** An [Instant] that is [serializable][Serializable] with [InstantInEpochSecondsSerializer]. */
// public typealias InstantInEpochSeconds = @Serializable(with = InstantInEpochSecondsSerializer::class) Instant
