package dev.kord.common.entity.optional

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

internal class OptionalLongTest {

    @Serializable
    class EmptyOptionalEntity(val value: OptionalLong = OptionalLong.Missing)

    @Test
    fun `deserializing nothing in optional assigns Missing`() {
        //language=json
        val json = """{}"""


        val entity = Json.decodeFromString<EmptyOptionalEntity>(json)

        assertIs<OptionalLong.Missing>(entity.value)
    }

    @Serializable
    class NullOptionalEntity(@Suppress("unused") val value: OptionalLong = OptionalLong.Missing)

    @Test
    fun `deserializing null in optional throws SerializationException`() {
        //language=json
        val json = """{ "value":null }"""


        assertFailsWith<SerializationException> {
            Json.decodeFromString<NullOptionalEntity>(json)
        }
    }

    @Serializable
    class ValueOptionalEntity(@Suppress("unused") val value: OptionalLong = OptionalLong.Missing)

    @Test
    fun `deserializing value in optional assigns Value`() {
        //language=json
        val json = """{ "value":5 }"""

        val entity = Json.decodeFromString<ValueOptionalEntity>(json)
        require(entity.value is OptionalLong.Value)

        assertEquals(5, entity.value.value)
    }
}
