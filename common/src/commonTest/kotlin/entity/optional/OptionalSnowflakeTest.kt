package dev.kord.common.entity.optional

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.*

internal class OptionalSnowflakeTest {


    @Serializable
    class EmptyOptionalEntity(val value: OptionalSnowflake = OptionalSnowflake.Missing)

    @Test
    @JsName("test1")
    fun `deserializing nothing in optional assigns Missing`() {
        //language=json
        val json = """{}"""


        val entity = Json.decodeFromString<EmptyOptionalEntity>(json)

        assertIs<OptionalSnowflake.Missing>(entity.value)
    }


    @Serializable
    class NullOptionalEntity(@Suppress("unused") val value: OptionalSnowflake = OptionalSnowflake.Missing)

    @Test
    @JsName("test2")
    fun `deserializing null in optional throws SerializationException`() {
        //language=json
        val json = """{ "value":null }"""

        assertFailsWith<SerializationException> {
            Json.decodeFromString<NullOptionalEntity>(json)
        }
    }


    @Serializable
    class ValueOptionalEntity(@Suppress("unused") val value: OptionalSnowflake = OptionalSnowflake.Missing)

    @Test
    @JsName("test3")
    fun `deserializing value in optional assigns Value`() {
        //language=test
        val json = """{ "value":5 }"""

        val entity = Json.decodeFromString<ValueOptionalEntity>(json)
        assertIs<OptionalSnowflake.Value>(entity.value)
        assertEquals(Snowflake(5u), entity.value.value)
    }

}
