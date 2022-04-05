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

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.kord.common.serialization.InstantInEpochMilliseconds", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {

        // if the result of toEpochMilliseconds() doesn't fit in the range of Long type, it is coerced into that range
        val valueInMillis = value.toEpochMilliseconds()

        val atLimit = when (valueInMillis) {
            Long.MIN_VALUE, Long.MAX_VALUE -> true
            else -> false
        }

        // construct Instant from valueInMillis and compare with original Instant to check
        // whether valueInMillis was exactly at limit or coerced into the range of Long type
        if (atLimit && (Instant.fromEpochMilliseconds(valueInMillis) != value)) throw SerializationException(
            "The Instant $value expressed as a number of milliseconds from the epoch Instant does not fit in the " +
                    "range of Long type and therefore cannot be serialized with InstantInEpochMillisecondsSerializer"
        )

        encoder.encodeLong(valueInMillis)
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochMilliseconds(decoder.decodeLong())
    }
}

/** An [Instant] that is [serializable][Serializable] with [InstantInEpochMillisecondsSerializer]. */
public typealias InstantInEpochMilliseconds = @Serializable(with = InstantInEpochMillisecondsSerializer::class) Instant


// epoch seconds

/** Serializer that encodes and decodes [Instant]s in [epoch seconds][Instant.epochSeconds]. */
public object InstantInEpochSecondsSerializer : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.kord.common.serialization.InstantInEpochSeconds", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        // epochSeconds always fits in the range of Long type and never coerces -> no need to check for overflow
        encoder.encodeLong(value.epochSeconds)
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochSeconds(decoder.decodeLong())
    }
}

/** An [Instant] that is [serializable][Serializable] with [InstantInEpochSecondsSerializer]. */
public typealias InstantInEpochSeconds = @Serializable(with = InstantInEpochSecondsSerializer::class) Instant
