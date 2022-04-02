package serialization

import dev.kord.common.serialization.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

abstract class DurationSerializerTest(
    private val json: String,
    private val duration: Duration,
    private val durationToRound: Duration,
    private val serializer: KSerializer<Duration>,
) {
    init {
        require(duration.isPositive())
        require(durationToRound.isPositive())
    }


    @Test
    fun `zero Duration can be serialized`() {
        val serialized = Json.encodeToString(serializer, Duration.ZERO)
        assertEquals(expected = "0", actual = serialized)
    }

    @Test
    fun `zero Duration can be deserialized`() {
        val deserialized = Json.decodeFromString(serializer, "0")
        assertEquals(expected = Duration.ZERO, actual = deserialized)
    }


    @Test
    fun `positive Duration can be serialized`() {
        val serialized = Json.encodeToString(serializer, duration)
        assertEquals(expected = json, actual = serialized)
    }

    @Test
    fun `positive Duration can be rounded and serialized`() {
        val serialized = Json.encodeToString(serializer, durationToRound)
        assertEquals(expected = json, actual = serialized)
    }

    @Test
    fun `positive Duration can be deserialized`() {
        val deserialized = Json.decodeFromString(serializer, json)
        assertEquals(expected = duration, actual = deserialized)
    }


    @Test
    fun `negative Duration can be serialized`() {
        val serialized = Json.encodeToString(serializer, -duration)
        assertEquals(expected = "-$json", actual = serialized)
    }

    @Test
    fun `negative Duration can be rounded and serialized`() {
        val serialized = Json.encodeToString(serializer, -durationToRound)
        assertEquals(expected = "-$json", actual = serialized)
    }

    @Test
    fun `negative Duration can be deserialized`() {
        val deserialized = Json.decodeFromString(serializer, "-$json")
        assertEquals(expected = -duration, actual = deserialized)
    }
}


class DurationInWholeNanosecondsSerializerTest : DurationSerializerTest(
    json = "84169",
    duration = 84169.nanoseconds,
    durationToRound = 84169.48.nanoseconds,
    serializer = DurationInWholeNanosecondsSerializer,
)

class DurationInWholeMicrosecondsSerializerTest : DurationSerializerTest(
    json = "25622456",
    duration = 25622456.microseconds,
    durationToRound = 25622456.4.microseconds,
    serializer = DurationInWholeMicrosecondsSerializer,
)

class DurationInWholeMillisecondsSerializerTest : DurationSerializerTest(
    json = "3495189",
    duration = 3495189.milliseconds,
    durationToRound = 3495189.24.milliseconds,
    serializer = DurationInWholeMillisecondsSerializer,
)

class DurationInWholeSecondsSerializerTest : DurationSerializerTest(
    json = "987465",
    duration = 987465.seconds,
    durationToRound = 987465.489.seconds,
    serializer = DurationInWholeSecondsSerializer,
)

class DurationInWholeMinutesSerializerTest : DurationSerializerTest(
    json = "24905",
    duration = 24905.minutes,
    durationToRound = 24905.164.minutes,
    serializer = DurationInWholeMinutesSerializer,
)

class DurationInWholeHoursSerializerTest : DurationSerializerTest(
    json = "7245",
    duration = 7245.hours,
    durationToRound = 7245.24.hours,
    serializer = DurationInWholeHoursSerializer,
)

class DurationInWholeDaysSerializerTest : DurationSerializerTest(
    json = "92",
    duration = 92.days,
    durationToRound = 92.12.days,
    serializer = DurationInWholeDaysSerializer,
)
