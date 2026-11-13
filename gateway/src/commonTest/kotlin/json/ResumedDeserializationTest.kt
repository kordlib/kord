package dev.kord.gateway.json

import dev.kord.gateway.Event
import dev.kord.gateway.Resumed
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class ResumedDeserializationTest {
    private val name = "\"RESUMED\""

    @Test
    fun test_Resumed_deserialization_without_sequence() {
        val resumed = Resumed(sequence = null)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0", "t" to name),
            jsonObjectPermutations("op" to "0", "t" to name, "s" to "null"),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(resumed, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_Resumed_deserialization_with_sequence() {
        val sequence = Random.nextInt()
        val resumed = Resumed(sequence)
        val permutations = jsonObjectPermutations("op" to "0", "t" to name, "s" to "$sequence")
        permutations.forEach { perm ->
            assertEquals(resumed, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    private val data =
        listOf("null", "1234", "true", "\"str\"", """[null,-1,false,""]""", """{"a":null,"b":-134,"c":true,"d":"x"}""")

    @Test
    fun test_Resumed_deserialization_ignores_data_without_sequence() = data.forEach { data ->
        val resumed = Resumed(sequence = null)
        val permutations = listOf(
            jsonObjectPermutations("op" to "0", "t" to name, "d" to data),
            jsonObjectPermutations("op" to "0", "t" to name, "s" to "null", "d" to data),
        ).flatten()
        permutations.forEach { perm ->
            assertEquals(resumed, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }

    @Test
    fun test_Resumed_deserialization_ignores_data_with_sequence() = data.forEach { data ->
        val sequence = Random.nextInt()
        val resumed = Resumed(sequence)
        val permutations = jsonObjectPermutations("op" to "0", "t" to name, "s" to "$sequence", "d" to data)
        permutations.forEach { perm ->
            assertEquals(resumed, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }
}
