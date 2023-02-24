package dev.kord.common.entity.optional

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

internal class OptionalTest {

    @Test
    @JsName("test1")
    fun `creating optional from nullable value returns Value on non-null value`() {
        val value = 5
        val optional = Optional(value)

        assertIs<Optional.Value<*>>(optional)
        assertEquals(optional.value, value)
    }

    @Test
    @JsName("test2")
    fun `creating optional from nullable value returns Null on null value`() {
        val value: Int? = null
        val optional = Optional(value)

        assertIs<Optional.Null<*>>(optional)
    }


    @Serializable
    private class NullOptionalEntity(val value: Optional<String?>)

    @Test
    @JsName("test3")
    fun `deserializing null in nullable optional assigns Null`() {
        //language=json
        val json = """{ "value":null }"""

        val entity = Json.decodeFromString<NullOptionalEntity>(json)

        assertIs<Optional.Null<*>>(entity.value)
    }


    @Serializable
    class EmptyOptionalEntity(val value: Optional<String?> = Optional.Missing())

    @Test
    @JsName("test4")
    fun `deserializing nothing in nullable optional assigns Missing`() {
        //language=json
        val json = """{}"""

        val entity = Json.decodeFromString<EmptyOptionalEntity>(json)

        assertIs<Optional.Missing<*>>(entity.value)
    }


    @Serializable
    class UnexpectedEmptyOptionalEntity(val value: Optional<String> = Optional.Missing())

    @Test
    @JsName("test5")
    fun `deserializing nothing in non-nullable optional assigns Missing`() {
        //language=json
        val json = """{}"""

        val entity = Json.decodeFromString<UnexpectedEmptyOptionalEntity>(json)

        assertIs<Optional.Missing<*>>(entity.value)
    }


    @Serializable
    private class UnexpectedNullOptionalEntity(@Suppress("unused") val value: Optional<String> = Optional.Missing())

    @Test
    @JsName("test6")
    fun `deserializing null in non-nullable optional throws SerializationException`() {
        //language=json
        val json = """{ "value":null }"""

        assertFailsWith<SerializationException> {
            Json.decodeFromString<UnexpectedNullOptionalEntity>(json)
        }
    }

}
