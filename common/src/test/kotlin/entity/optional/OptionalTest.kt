package dev.kord.common.entity.optional

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

internal class OptionalTest {

    @Test
    fun `creating optional from nullable value returns Value on non-null value`() {
        val value: Int? = 5
        val optional = Optional(value)

        assert(optional is Optional.Value)
        assert((optional as Optional.Value).value == value)
    }

    @Test
    fun `creating optional from nullable value returns Null on null value`() {
        val value: Int? = null
        val optional = Optional(value)

        assert(optional is Optional.Null)
    }


    @Serializable
    private class NullOptionalEntity(val value: Optional<String?>)

    @Test
    fun `deserializing null in nullable optional assigns Null`() {
        @Language("json")
        val json = """{ "value":null }"""

        val entity = Json.decodeFromString<NullOptionalEntity>(json)

        assert(entity.value is Optional.Null)
    }


    @Serializable
    class EmptyOptionalEntity(val value: Optional<String?> = Optional.Missing())

    @Test
    fun `deserializing nothing in nullable optional assigns Missing`() {
        @Language("json")
        val json = """{}"""

        val entity = Json.decodeFromString<EmptyOptionalEntity>(json)

        assert(entity.value is Optional.Missing)
    }


    @Serializable
    class UnexpectedEmptyOptionalEntity(val value: Optional<String> = Optional.Missing())

    @Test
    fun `deserializing nothing in non-nullable optional assigns Missing`() {
        @Language("json")
        val json = """{}"""

        val entity = Json.decodeFromString<UnexpectedEmptyOptionalEntity>(json)

        assert(entity.value is Optional.Missing)
    }


    @Serializable
    private class UnexpectedNullOptionalEntity(@Suppress("unused") val value: Optional<String> = Optional.Missing())

    @Test
    fun `deserializing null in non-nullable optional throws SerializationException`() {
        @Language("json")
        val json = """{ "value":null }"""

        org.junit.jupiter.api.assertThrows<SerializationException> {
            Json.decodeFromString<UnexpectedNullOptionalEntity>(json)
        }
    }

}