package dev.kord.gateway.json

import dev.kord.gateway.Event
import dev.kord.gateway.UnknownDispatchEvent
import kotlinx.serialization.json.*
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class UnknownDispatchEventDeserializationTest {
    private val eventName = "SOME_UNKNOWN_EVENT"
    private val jsonAndData = listOf(
        "null" to JsonNull,
        "1234" to JsonPrimitive(1234),
        "true" to JsonPrimitive(true),
        "\"str\"" to JsonPrimitive("str"),
        """[null,-1,false,""]""" to buildJsonArray {
            add(JsonNull)
            add(-1)
            add(false)
            add("")
        },
        """{"a":null,"b":-134,"c":true,"d":"x"}""" to buildJsonObject {
            put("a", JsonNull)
            put("b", -134)
            put("c", true)
            put("d", "x")
        },
    )

    @Test
    fun test_empty_UnknownDispatchEvent_deserialization() {
        val emptyEvent = UnknownDispatchEvent(name = null, data = JsonNull, sequence = null)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0"),
            jsonObjectPermutations("op" to "0", "t" to "null"),
            jsonObjectPermutations("op" to "0", "s" to "null"),
            jsonObjectPermutations("op" to "0", "d" to "null"),
            jsonObjectPermutations("op" to "0", "t" to "null", "s" to "null"),
            jsonObjectPermutations("op" to "0", "t" to "null", "d" to "null"),
            jsonObjectPermutations("op" to "0", "s" to "null", "d" to "null"),
            jsonObjectPermutations("op" to "0", "t" to "null", "s" to "null", "d" to "null"),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(emptyEvent, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_name_only_UnknownDispatchEvent_deserialization() {
        val nameOnlyEvent = UnknownDispatchEvent(eventName, data = JsonNull, sequence = null)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\""),
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "s" to "null"),
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "d" to "null"),
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "s" to "null", "d" to "null"),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(nameOnlyEvent, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_sequence_only_UnknownDispatchEvent_deserialization() {
        val sequence = Random.nextInt()
        val sequenceOnlyEvent = UnknownDispatchEvent(name = null, data = JsonNull, sequence)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0", "s" to "$sequence"),
            jsonObjectPermutations("op" to "0", "t" to "null", "s" to "$sequence"),
            jsonObjectPermutations("op" to "0", "s" to "$sequence", "d" to "null"),
            jsonObjectPermutations("op" to "0", "t" to "null", "s" to "$sequence", "d" to "null"),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(sequenceOnlyEvent, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_data_only_UnknownDispatchEvent_deserialization() = jsonAndData.forEach { (json, data) ->
        val dataOnlyEvent = UnknownDispatchEvent(name = null, data, sequence = null)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0", "d" to json),
            jsonObjectPermutations("op" to "0", "t" to "null", "d" to json),
            jsonObjectPermutations("op" to "0", "s" to "null", "d" to json),
            jsonObjectPermutations("op" to "0", "t" to "null", "s" to "null", "d" to json),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(dataOnlyEvent, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_name_and_sequence_UnknownDispatchEvent_deserialization() {
        val sequence = Random.nextInt()
        val nameAndSequenceEvent = UnknownDispatchEvent(eventName, data = JsonNull, sequence)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "s" to "$sequence"),
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "s" to "$sequence", "d" to "null"),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(nameAndSequenceEvent, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_name_and_data_UnknownDispatchEvent_deserialization() = jsonAndData.forEach { (json, data) ->
        val nameAndDataEvent = UnknownDispatchEvent(eventName, data, sequence = null)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "d" to json),
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "s" to "null", "d" to json),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(nameAndDataEvent, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_sequence_and_data_UnknownDispatchEvent_deserialization() = jsonAndData.forEach { (json, data) ->
        val sequence = Random.nextInt()
        val sequenceAndDataEvent = UnknownDispatchEvent(name = null, data, sequence)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0", "s" to "$sequence", "d" to json),
            jsonObjectPermutations("op" to "0", "t" to "null", "s" to "$sequence", "d" to json),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(sequenceAndDataEvent, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_full_UnknownDispatchEvent_deserialization() = jsonAndData.forEach { (json, data) ->
        val sequence = Random.nextInt()
        val fullEvent = UnknownDispatchEvent(eventName, data, sequence)
        val permutations = jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "s" to "$sequence", "d" to json)
        permutations.forEach { perm ->
            assertEquals(fullEvent, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }
}
