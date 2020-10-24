package com.gitlab.kordlib.common.entity.optional

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class OptionalBooleanTest {

    @Test
    fun `deserializing nothing in optional assigns Missing`(){
        @Language("json")
        val json = """{}"""

        @Serializable
        class Entity(val value: OptionalBoolean = OptionalBoolean.Missing)

        val entity = Json.decodeFromString<Entity>(json)

        assert(entity.value is OptionalBoolean.Missing)
    }

    @Test
    fun `deserializing null in optional throws SerializationException`(){
        @Language("json")
        val json = """{ "value":null }"""

        @Serializable
        class Entity(@Suppress("unused") val value: OptionalBoolean = OptionalBoolean.Missing)

        org.junit.jupiter.api.assertThrows<SerializationException> {
            Json.decodeFromString<Entity>(json)
        }
    }

    @Test
    fun `deserializing value in optional assigns Value`(){
        @Language("json")
        val json = """{ "value":true }"""

        @Serializable
        class Entity(@Suppress("unused") val value: OptionalBoolean = OptionalBoolean.Missing)

        val entity = Json.decodeFromString<Entity>(json)
        require(entity.value is OptionalBoolean.Value)

        Assertions.assertEquals(true, entity.value.value)
    }

}
