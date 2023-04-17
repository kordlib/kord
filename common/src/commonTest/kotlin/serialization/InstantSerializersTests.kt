package dev.kord.common.serialization

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

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
    @JsName("test1")
    fun `epoch Instant can be serialized`() {
        assertEquals(expected = "0", actual = serialize(EPOCH))
    }

    @Test
    @JsName("test2")
    fun `epoch Instant can be deserialized`() {
        assertEquals(expected = EPOCH, actual = deserialize("0"))
    }


    @Test
    @JsName("test3")
    fun `future Instant can be serialized`() {
        assertEquals(expected = json, actual = serialize(instant))
    }

    @Test
    @JsName("test4")
    fun `future Instant can be deserialized`() {
        assertEquals(expected = instant, actual = deserialize(json))
    }


    @Test
    @JsName("test5")
    fun `past Instant can be serialized`() {
        assertEquals(expected = "-$json", actual = serialize(mirroredInstant))
    }

    @Test
    @JsName("test6")
    fun `past Instant can be deserialized`() {
        assertEquals(expected = mirroredInstant, actual = deserialize("-$json"))
    }
}


class InstantInEpochMillisecondsSerializerTest : InstantSerializerTest(
    json = "796514689159",
    instant = Instant.fromEpochMilliseconds(796514689159),
    serializer = InstantInEpochMillisecondsSerializer,
) {
    // workaround for https://github.com/Kotlin/kotlinx-datetime/issues/263, use normal operators when fix is released
    private infix fun Instant.plus(duration: Duration) = (this + 1.seconds) - (1.seconds - duration)
    private infix fun Instant.minus(duration: Duration) = (this - 1.seconds) + (1.seconds - duration)


    private val past = Instant.fromEpochMilliseconds(Long.MIN_VALUE)
    private val future = Instant.fromEpochMilliseconds(Long.MAX_VALUE)

    // platform-dependent
    private val pastClamped = past.toEpochMilliseconds() != Long.MIN_VALUE
    private val futureClamped = future.toEpochMilliseconds() != Long.MAX_VALUE

    private val clampedMin = Instant.fromEpochSeconds(Long.MIN_VALUE, Long.MIN_VALUE).toEpochMilliseconds()
    private val clampedMax = Instant.fromEpochSeconds(Long.MAX_VALUE, Long.MAX_VALUE).toEpochMilliseconds()

    @Test
    @JsName("test7")
    fun `future Instant under limit can be serialized`() {
        assertEquals(
            expected = (if (futureClamped) clampedMax else Long.MAX_VALUE - 1).toString(),
            actual = serialize(future minus 1.nanoseconds),
        )
    }

    @Test
    @JsName("test8")
    fun `past Instant under limit can be serialized`() {
        assertEquals(
            expected = (if (pastClamped) clampedMin else Long.MIN_VALUE).toString(),
            actual = serialize(past plus 1.nanoseconds),
        )
    }


    @Test
    @JsName("test9")
    fun `future Instant exactly at limit can be serialized`() {
        assertEquals(
            expected = (if (futureClamped) clampedMax else Long.MAX_VALUE).toString(),
            actual = serialize(future),
        )
    }

    @Test
    @JsName("test10")
    fun `past Instant exactly at limit can be serialized`() {
        assertEquals(
            expected = (if (pastClamped) clampedMin else Long.MIN_VALUE).toString(),
            actual = serialize(past),
        )
    }


    @Test
    @JsName("test11")
    fun `future Instant over limit cannot be serialized`() {
        if (!futureClamped) assertFailsWith<SerializationException> { serialize(future plus 1.nanoseconds) }
    }

    @Test
    @JsName("test12")
    fun `past Instant over limit cannot be serialized`() {
        if (!pastClamped) assertFailsWith<SerializationException> { serialize(past minus 1.nanoseconds) }
    }
}

class InstantInEpochSecondsSerializerTest : InstantSerializerTest(
    json = "3992794563",
    instant = Instant.fromEpochSeconds(3992794563),
    serializer = InstantInEpochSecondsSerializer,
)
