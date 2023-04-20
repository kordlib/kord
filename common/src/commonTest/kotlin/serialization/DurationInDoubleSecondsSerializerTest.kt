package dev.kord.common.serialization

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class DurationInDoubleSecondsSerializerTest {
    private fun serialize(duration: Duration) = Json.encodeToString(DurationInDoubleSecondsSerializer, duration)
    private fun deserialize(json: String) = Json.decodeFromString(DurationInDoubleSecondsSerializer, json)


    @Test
    fun zero_Duration_can_be_serialized() {
        assertEquals(expected = 0.0.toString(), actual = serialize(Duration.ZERO))
    }

    @Test
    fun zero_Duration_can_be_deserialized() {
        for (jsonZero in listOf("0", "0.0", "0.0000", "0.00e-0864", "0E+456")) {
            assertEquals(expected = Duration.ZERO, actual = deserialize(jsonZero))
        }
    }


    @Test
    fun infinite_Durations_cannot_be_serialized() {
        assertFailsWith<SerializationException> { serialize(Duration.INFINITE) }
        assertFailsWith<SerializationException> { serialize(-Duration.INFINITE) }
    }

    private val largestFiniteDuration = (Long.MAX_VALUE / 2 - 1).milliseconds

    init {
        check(largestFiniteDuration.isFinite())
        check(largestFiniteDuration + (1.milliseconds - 1.nanoseconds) == largestFiniteDuration)
        check((largestFiniteDuration + 1.milliseconds).isInfinite())
    }

    @Test
    fun largest_finite_Durations_can_be_serialized() {
        assertEquals(4.611686018427388e+15.toString(), serialize(largestFiniteDuration))
        assertEquals((-4.611686018427388e+15).toString(), serialize(-largestFiniteDuration))
    }


    private val duration2Jsons = listOf(
        123.seconds to listOf(123.0.toString(), "123", "0.1230E+3", "1230.0e-1"),
        5646.876456.seconds to listOf("5646.876456", "5646.87645600", "5.646876456e003"),
        4631.89.seconds to listOf("4631.89", "4631.890000000", "46.3189000000E2"),
        4.595632e+1.seconds to listOf("45.95632", "4.595632e+1"),
    )

    @Test
    fun positive_Durations_can_be_serialized() {
        for ((duration, jsons) in duration2Jsons) {
            assertEquals(expected = jsons.first(), actual = serialize(duration))
        }
    }

    @Test
    fun positive_Durations_can_be_deserialized() {
        for ((duration, jsons) in duration2Jsons) {
            for (json in jsons) {
                assertEquals(expected = duration, actual = deserialize(json))
            }
        }
    }

    @Test
    fun negative_Durations_can_be_serialized() {
        for ((duration, jsons) in duration2Jsons) {
            assertEquals(expected = "-${jsons.first()}", actual = serialize(-duration))
        }
    }

    @Test
    fun negative_Durations_can_be_deserialized() {
        for ((duration, jsons) in duration2Jsons) {
            for (json in jsons) {
                assertEquals(expected = -duration, actual = deserialize("-$json"))
            }
        }
    }


    private val largeJson = "4611686018427388" // MAX_MILLIS / 1_000 + 1

    @Test
    fun large_positive_Duration_gets_deserialized_as_Infinity() {
        assertEquals(expected = Duration.INFINITE, deserialize(largeJson))
    }

    @Test
    fun large_negative_Duration_gets_deserialized_as_negative_Infinity() {
        assertEquals(expected = -Duration.INFINITE, deserialize("-$largeJson"))
    }
}
