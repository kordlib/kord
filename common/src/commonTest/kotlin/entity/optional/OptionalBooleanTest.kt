package dev.kord.common.entity.optional

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class OptionalBooleanTest {

    @Serializable
    private class EmptyOptionalEntity(val value: OptionalBoolean = OptionalBoolean.Missing)

    @Test
    @JsName("test1")
    fun `deserializing nothing in optional assigns Missing`(){
        //language=json
        val json = """{}"""

        val entity = Json.decodeFromString<EmptyOptionalEntity>(json)

        assertIs<OptionalBoolean.Missing>(entity.value)
    }

    @Serializable
    private class NullOptionalEntity(@Suppress("unused") val value: OptionalBoolean = OptionalBoolean.Missing)

    @Test
    @JsName("test2")
    fun `deserializing null in optional throws SerializationException`(){
        //language=json
        val json = """{ "value":null }"""

        assertFailsWith<SerializationException> {
            Json.decodeFromString<NullOptionalEntity>(json)
        }
    }

    @Serializable
    private class ValueOptionalEntity(@Suppress("unused") val value: OptionalBoolean = OptionalBoolean.Missing)

    @Test
    @JsName("test3")
    fun `deserializing value in optional assigns Value`(){
        //language=json
        val json = """{ "value":true }"""


        val entity = Json.decodeFromString<ValueOptionalEntity>(json)
        assertIs<OptionalBoolean.Value>(entity.value)

        assertTrue(entity.value.value)
    }

}
