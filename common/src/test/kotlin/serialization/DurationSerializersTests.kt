package serialization

import dev.kord.common.serialization.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit.MILLISECONDS

abstract class DurationSerializerTest(
    private val json: String,
    private val duration: Duration,
    private val durationToRound: Duration,
    private val durationThatWouldOverflowInTargetUnit: Duration? = null,
    private val largeJson: String? = null,
    private val serializer: DurationAsLongSerializer,
) {
    init {
        require(duration.isPositive())
        require(durationToRound.isPositive())

        if (serializer.unit < MILLISECONDS) {
            require(durationThatWouldOverflowInTargetUnit != null)
            require(durationThatWouldOverflowInTargetUnit.isPositive())
            require(largeJson == null)
        } else {
            require(durationThatWouldOverflowInTargetUnit == null)
            require(largeJson != null)
        }
    }

    private fun serialize(duration: Duration) = Json.encodeToString(serializer, duration)
    private fun deserialize(json: String) = Json.decodeFromString(serializer, json)


    @Test
    fun `zero Duration can be serialized`() {
        assertEquals(expected = "0", actual = serialize(Duration.ZERO))
    }

    @Test
    fun `zero Duration can be deserialized`() {
        assertEquals(expected = Duration.ZERO, actual = deserialize("0"))
    }


    @Test
    fun `infinite Durations cannot be serialized`() {
        assertFailsWith<SerializationException> { serialize(Duration.INFINITE) }
        assertFailsWith<SerializationException> { serialize(-Duration.INFINITE) }
    }


    @Test
    fun `positive Duration can be serialized`() {
        assertEquals(expected = json, actual = serialize(duration))
    }

    @Test
    fun `positive Duration can be rounded and serialized`() {
        assertEquals(expected = json, actual = serialize(durationToRound))
    }

    @Test
    fun `positive Duration can be deserialized`() {
        assertEquals(expected = duration, actual = deserialize(json))
    }


    @Test
    fun `negative Duration can be serialized`() {
        assertEquals(expected = "-$json", actual = serialize(-duration))
    }

    @Test
    fun `negative Duration can be rounded and serialized`() {
        assertEquals(expected = "-$json", actual = serialize(-durationToRound))
    }

    @Test
    fun `negative Duration can be deserialized`() {
        assertEquals(expected = -duration, actual = deserialize("-$json"))
    }


    @Test
    fun `positive Duration that would overflow in target unit cannot be serialized`() {
        if (durationThatWouldOverflowInTargetUnit != null) assertFailsWith<SerializationException> {
            serialize(durationThatWouldOverflowInTargetUnit)
        }
    }

    @Test
    fun `negative Duration that would overflow in target unit cannot be serialized`() {
        if (durationThatWouldOverflowInTargetUnit != null) assertFailsWith<SerializationException> {
            serialize(-durationThatWouldOverflowInTargetUnit)
        }
    }


    @Test
    fun `large positive Duration gets deserialized as Infinity`() {
        if (largeJson != null) assertEquals(expected = Duration.INFINITE, deserialize(largeJson))
    }

    @Test
    fun `large negative Duration gets deserialized as -Infinity`() {
        if (largeJson != null) assertEquals(expected = -Duration.INFINITE, deserialize("-$largeJson"))
    }
}


class DurationInNanosecondsSerializerTest : DurationSerializerTest(
    json = "84169",
    duration = 84169.nanoseconds,
    durationToRound = 84169.48.nanoseconds,
    // use Long.MAX_VALUE / 1_000_000 + 1 (the smallest value that would overflow when multiplied by 1_000_000)
    //                      Long.MAX_VALUE: 9_223_372_036_854_775_807
    durationThatWouldOverflowInTargetUnit = 9_223_372_036_855.milliseconds,
    serializer = DurationInNanosecondsSerializer,
)

class DurationInMicrosecondsSerializerTest : DurationSerializerTest(
    json = "25622456",
    duration = 25622456.microseconds,
    durationToRound = 25622456.4.microseconds,
    // use Long.MAX_VALUE / 1_000 + 1 (the smallest value that would overflow when multiplied by 1_000)
    //                      Long.MAX_VALUE: 9_223_372_036_854_775_807
    durationThatWouldOverflowInTargetUnit = 9_223_372_036_854_776.milliseconds,
    serializer = DurationInMicrosecondsSerializer,
)

class DurationInMillisecondsSerializerTest : DurationSerializerTest(
    json = "3495189",
    duration = 3495189.milliseconds,
    durationToRound = 3495189.24.milliseconds,
    largeJson = "4611686018427387903", // the Duration implementation internal `MAX_MILLIS`
    serializer = DurationInMillisecondsSerializer,
)

class DurationInSecondsSerializerTest : DurationSerializerTest(
    json = "987465",
    duration = 987465.seconds,
    durationToRound = 987465.489.seconds,
    largeJson = "4611686018427388", // MAX_MILLIS / 1_000 + 1
    serializer = DurationInSecondsSerializer,
)

class DurationInMinutesSerializerTest : DurationSerializerTest(
    json = "24905",
    duration = 24905.minutes,
    durationToRound = 24905.164.minutes,
    largeJson = "76861433640457", // MAX_MILLIS / 1_000 / 60 + 1
    serializer = DurationInMinutesSerializer,
)

class DurationInHoursSerializerTest : DurationSerializerTest(
    json = "7245",
    duration = 7245.hours,
    durationToRound = 7245.24.hours,
    largeJson = "1281023894008", // MAX_MILLIS / 1_000 / 60 / 60 + 1
    serializer = DurationInHoursSerializer,
)

class DurationInDaysSerializerTest : DurationSerializerTest(
    json = "92",
    duration = 92.days,
    durationToRound = 92.12.days,
    largeJson = "53375995584", // MAX_MILLIS / 1_000 / 60 / 60 / 24 + 1
    serializer = DurationInDaysSerializer,
)
