package dev.kord.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.*
import kotlin.time.toDuration


/** Serializer that encodes and decodes [Duration]s as a [Long] number of the specified [unit]. */
public sealed class DurationAsLongSerializer(
    public val unit: DurationUnit,
    private val name: String,
) : KSerializer<Duration> {

    final override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.kord.common.serialization.$name", PrimitiveKind.LONG)

    final override fun serialize(encoder: Encoder, value: Duration) {
        when (val valueAsLong = value.toLong(unit)) {

            Long.MIN_VALUE, Long.MAX_VALUE -> throw SerializationException(
                if (value.isInfinite()) {
                    "Infinite Durations cannot be serialized, got $value"
                } else {
                    "The Duration $value expressed as a number of ${
                        unit.name.lowercase()
                    } does not fit in the range of Long type and therefore cannot be serialized with ${name}Serializer"
                }
            )

            else -> encoder.encodeLong(valueAsLong)
        }
    }

    final override fun deserialize(decoder: Decoder): Duration {
        return decoder.decodeLong().toDuration(unit)
    }
}


// nanoseconds

/** Serializer that encodes and decodes [Duration]s in [whole nanoseconds][Duration.inWholeNanoseconds]. */
public object DurationInNanosecondsSerializer : DurationAsLongSerializer(NANOSECONDS, "DurationInNanoseconds")

/** A [Duration] that is [serializable][Serializable] with [DurationInNanosecondsSerializer]. */
public typealias DurationInNanoseconds = @Serializable(with = DurationInNanosecondsSerializer::class) Duration


// microseconds

/** Serializer that encodes and decodes [Duration]s in [whole microseconds][Duration.inWholeMicroseconds]. */
public object DurationInMicrosecondsSerializer : DurationAsLongSerializer(MICROSECONDS, "DurationInMicroseconds")

/** A [Duration] that is [serializable][Serializable] with [DurationInMicrosecondsSerializer]. */
public typealias DurationInMicroseconds = @Serializable(with = DurationInMicrosecondsSerializer::class) Duration


// milliseconds

/** Serializer that encodes and decodes [Duration]s in [whole milliseconds][Duration.inWholeMilliseconds]. */
public object DurationInMillisecondsSerializer : DurationAsLongSerializer(MILLISECONDS, "DurationInMilliseconds")

/** A [Duration] that is [serializable][Serializable] with [DurationInMillisecondsSerializer]. */
public typealias DurationInMilliseconds = @Serializable(with = DurationInMillisecondsSerializer::class) Duration


// seconds

/** Serializer that encodes and decodes [Duration]s in [whole seconds][Duration.inWholeSeconds]. */
public object DurationInSecondsSerializer : DurationAsLongSerializer(SECONDS, "DurationInSeconds")

/** A [Duration] that is [serializable][Serializable] with [DurationInSecondsSerializer]. */
public typealias DurationInSeconds = @Serializable(with = DurationInSecondsSerializer::class) Duration


// minutes

/** Serializer that encodes and decodes [Duration]s in [whole minutes][Duration.inWholeMinutes]. */
public object DurationInMinutesSerializer : DurationAsLongSerializer(MINUTES, "DurationInMinutes")

/** A [Duration] that is [serializable][Serializable] with [DurationInMinutesSerializer]. */
public typealias DurationInMinutes = @Serializable(with = DurationInMinutesSerializer::class) Duration


// hours

/** Serializer that encodes and decodes [Duration]s in [whole hours][Duration.inWholeHours]. */
public object DurationInHoursSerializer : DurationAsLongSerializer(HOURS, "DurationInHours")

/** A [Duration] that is [serializable][Serializable] with [DurationInHoursSerializer]. */
public typealias DurationInHours = @Serializable(with = DurationInHoursSerializer::class) Duration


// days

/** Serializer that encodes and decodes [Duration]s in [whole days][Duration.inWholeDays]. */
public object DurationInDaysSerializer : DurationAsLongSerializer(DAYS, "DurationInDays")

/** A [Duration] that is [serializable][Serializable] with [DurationInDaysSerializer]. */
public typealias DurationInDays = @Serializable(with = DurationInDaysSerializer::class) Duration
