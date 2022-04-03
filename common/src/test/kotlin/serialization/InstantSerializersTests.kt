package serialization

import dev.kord.common.serialization.InstantInEpochMillisecondsSerializer
import dev.kord.common.serialization.InstantInEpochSecondsSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

private val EPOCH = Instant.fromEpochSeconds(0)

abstract class InstantSerializerTest(
    private val json: String,
    private val instant: Instant,
    private val serializer: KSerializer<Instant>,
) {

    @Test
    fun `epoch Instant can be serialized`() {
        val serialized = Json.encodeToString(serializer, EPOCH)
        assertEquals(expected = "0", actual = serialized)
    }

    @Test
    fun `epoch Instant can be deserialized`() {
        val deserialized = Json.decodeFromString(serializer, "0")
        assertEquals(expected = EPOCH, actual = deserialized)
    }


    @Test
    fun `Instant can be serialized`() {
        val serialized = Json.encodeToString(serializer, instant)
        assertEquals(expected = json, actual = serialized)
    }

    @Test
    fun `Instant can be deserialized`() {
        val deserialized = Json.decodeFromString(serializer, json)
        assertEquals(expected = instant, actual = deserialized)
    }
}


class InstantInEpochMillisecondsSerializerTest : InstantSerializerTest(
    json = "796514689159",
    instant = Instant.fromEpochMilliseconds(796514689159),
    serializer = InstantInEpochMillisecondsSerializer,
)

class InstantInEpochSecondsSerializerTest : InstantSerializerTest(
    json = "3992794563",
    instant = Instant.fromEpochSeconds(3992794563),
    serializer = InstantInEpochSecondsSerializer,
)
