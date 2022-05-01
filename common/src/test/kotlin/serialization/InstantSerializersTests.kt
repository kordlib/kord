package serialization

import dev.kord.common.serialization.InstantInEpochMillisecondsSerializer
import dev.kord.common.serialization.InstantInEpochSecondsSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.nanoseconds

private val EPOCH = Instant.fromEpochSeconds(0)

abstract class InstantSerializerTest(
    private val json: String,
    private val instant: Instant,
    private val serializer: KSerializer<Instant>,
) {
    init {
        require(instant > EPOCH)
    }

    private val mirroredInstant = EPOCH - (instant - EPOCH)

    protected fun serialize(instant: Instant) = Json.encodeToString(serializer, instant)
    private fun deserialize(json: String) = Json.decodeFromString(serializer, json)


    @Test
    fun `epoch Instant can be serialized`() {
        assertEquals(expected = "0", actual = serialize(EPOCH))
    }

    @Test
    fun `epoch Instant can be deserialized`() {
        assertEquals(expected = EPOCH, actual = deserialize("0"))
    }


    @Test
    fun `future Instant can be serialized`() {
        assertEquals(expected = json, actual = serialize(instant))
    }

    @Test
    fun `future Instant can be deserialized`() {
        assertEquals(expected = instant, actual = deserialize(json))
    }


    @Test
    fun `past Instant can be serialized`() {
        assertEquals(expected = "-$json", actual = serialize(mirroredInstant))
    }

    @Test
    fun `past Instant can be deserialized`() {
        assertEquals(expected = mirroredInstant, actual = deserialize("-$json"))
    }
}


class InstantInEpochMillisecondsSerializerTest : InstantSerializerTest(
    json = "796514689159",
    instant = Instant.fromEpochMilliseconds(796514689159),
    serializer = InstantInEpochMillisecondsSerializer,
) {
    private val futureInstantExactlyAtLimit = Instant.fromEpochMilliseconds(Long.MAX_VALUE)
    private val pastInstantExactlyAtLimit = Instant.fromEpochMilliseconds(Long.MIN_VALUE)

    @Test
    fun `future Instant under limit can be serialized`() {
        assertEquals(
            expected = (Long.MAX_VALUE - 1).toString(),
            actual = serialize(futureInstantExactlyAtLimit - 1.nanoseconds),
        )
    }

    @Test
    fun `past Instant under limit can be serialized`() {
        assertEquals(
            expected = Long.MIN_VALUE.toString(),
            actual = serialize(pastInstantExactlyAtLimit + 1.nanoseconds),
        )
    }


    @Test
    fun `future Instant exactly at limit can be serialized`() {
        assertEquals(expected = Long.MAX_VALUE.toString(), actual = serialize(futureInstantExactlyAtLimit))
    }

    @Test
    fun `past Instant exactly at limit can be serialized`() {
        assertEquals(expected = Long.MIN_VALUE.toString(), actual = serialize(pastInstantExactlyAtLimit))
    }


    @Test
    fun `future Instant over limit cannot be serialized`() {
        assertFailsWith<SerializationException> { serialize(futureInstantExactlyAtLimit + 1.nanoseconds) }
    }

    @Test
    fun `past Instant over limit cannot be serialized`() {
        assertFailsWith<SerializationException> { serialize(pastInstantExactlyAtLimit - 1.nanoseconds) }
    }
}

class InstantInEpochSecondsSerializerTest : InstantSerializerTest(
    json = "3992794563",
    instant = Instant.fromEpochSeconds(3992794563),
    serializer = InstantInEpochSecondsSerializer,
)
