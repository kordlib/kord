package dev.kord.common.serialization

import kotlinx.serialization.KSerializer
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


/** Serializer that encodes and decodes [Duration]s in [whole nanoseconds][Duration.inWholeNanoseconds]. */
public object DurationInWholeNanosecondsSerializer : DurationSerializer(NANOSECONDS, "DurationInWholeNanoseconds")

/** Serializer that encodes and decodes [Duration]s in [whole microseconds][Duration.inWholeMicroseconds]. */
public object DurationInWholeMicrosecondsSerializer : DurationSerializer(MICROSECONDS, "DurationInWholeMicroseconds")

/** Serializer that encodes and decodes [Duration]s in [whole milliseconds][Duration.inWholeMilliseconds]. */
public object DurationInWholeMillisecondsSerializer : DurationSerializer(MILLISECONDS, "DurationInWholeMilliseconds")

/** Serializer that encodes and decodes [Duration]s in [whole seconds][Duration.inWholeSeconds]. */
public object DurationInWholeSecondsSerializer : DurationSerializer(SECONDS, "DurationInWholeSeconds")

/** Serializer that encodes and decodes [Duration]s in [whole minutes][Duration.inWholeMinutes]. */
public object DurationInWholeMinutesSerializer : DurationSerializer(MINUTES, "DurationInWholeMinutes")

/** Serializer that encodes and decodes [Duration]s in [whole hours][Duration.inWholeHours]. */
public object DurationInWholeHoursSerializer : DurationSerializer(HOURS, "DurationInWholeHours")

/** Serializer that encodes and decodes [Duration]s in [whole days][Duration.inWholeDays]. */
public object DurationInWholeDaysSerializer : DurationSerializer(DAYS, "DurationInWholeDays")
