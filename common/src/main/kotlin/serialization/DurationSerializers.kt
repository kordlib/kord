package dev.kord.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.*
import kotlin.time.toDuration


/** Serializer that encodes and decodes [Duration]s. */
public sealed class DurationSerializer(private val unit: DurationUnit, name: String) : KSerializer<Duration> {

    final override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("dev.kord.common.serialization.$name", PrimitiveKind.LONG)

    final override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeLong(value.toLong(unit))
    }

    final override fun deserialize(decoder: Decoder): Duration {
        return decoder.decodeLong().toDuration(unit)
    }
}


// nanoseconds

/** Serializer that encodes and decodes [Duration]s in [whole nanoseconds][Duration.inWholeNanoseconds]. */
public object DurationInWholeNanosecondsSerializer : DurationSerializer(NANOSECONDS, "DurationInWholeNanoseconds")

/** A [Duration] that is [serializable][Serializable] with [DurationInWholeNanosecondsSerializer]. */
public typealias DurationInWholeNanoseconds = @Serializable(with = DurationInWholeNanosecondsSerializer::class) Duration


// microseconds

/** Serializer that encodes and decodes [Duration]s in [whole microseconds][Duration.inWholeMicroseconds]. */
public object DurationInWholeMicrosecondsSerializer : DurationSerializer(MICROSECONDS, "DurationInWholeMicroseconds")

/** A [Duration] that is [serializable][Serializable] with [DurationInWholeMicrosecondsSerializer]. */
public typealias DurationInWholeMicroseconds = @Serializable(with = DurationInWholeMicrosecondsSerializer::class) Duration


// milliseconds

/** Serializer that encodes and decodes [Duration]s in [whole milliseconds][Duration.inWholeMilliseconds]. */
public object DurationInWholeMillisecondsSerializer : DurationSerializer(MILLISECONDS, "DurationInWholeMilliseconds")

/** A [Duration] that is [serializable][Serializable] with [DurationInWholeMillisecondsSerializer]. */
public typealias DurationInWholeMilliseconds = @Serializable(with = DurationInWholeMillisecondsSerializer::class) Duration


// seconds

/** Serializer that encodes and decodes [Duration]s in [whole seconds][Duration.inWholeSeconds]. */
public object DurationInWholeSecondsSerializer : DurationSerializer(SECONDS, "DurationInWholeSeconds")

/** A [Duration] that is [serializable][Serializable] with [DurationInWholeSecondsSerializer]. */
public typealias DurationInWholeSeconds = @Serializable(with = DurationInWholeSecondsSerializer::class) Duration


// minutes

/** Serializer that encodes and decodes [Duration]s in [whole minutes][Duration.inWholeMinutes]. */
public object DurationInWholeMinutesSerializer : DurationSerializer(MINUTES, "DurationInWholeMinutes")

/** A [Duration] that is [serializable][Serializable] with [DurationInWholeMinutesSerializer]. */
public typealias DurationInWholeMinutes = @Serializable(with = DurationInWholeMinutesSerializer::class) Duration


// hours

/** Serializer that encodes and decodes [Duration]s in [whole hours][Duration.inWholeHours]. */
public object DurationInWholeHoursSerializer : DurationSerializer(HOURS, "DurationInWholeHours")

/** A [Duration] that is [serializable][Serializable] with [DurationInWholeHoursSerializer]. */
public typealias DurationInWholeHours = @Serializable(with = DurationInWholeHoursSerializer::class) Duration


// days

/** Serializer that encodes and decodes [Duration]s in [whole days][Duration.inWholeDays]. */
public object DurationInWholeDaysSerializer : DurationSerializer(DAYS, "DurationInWholeDays")

/** A [Duration] that is [serializable][Serializable] with [DurationInWholeDaysSerializer]. */
public typealias DurationInWholeDays = @Serializable(with = DurationInWholeDaysSerializer::class) Duration
