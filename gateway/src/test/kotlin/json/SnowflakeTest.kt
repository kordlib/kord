package json

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

class SnowflakeTest {

    @Serializable
    private data class SnowflakeContainer(val snowflake: Snowflake)

    @Test
    fun `Deserialization of Snowflake as String completes successfully`() {
        val value = "1337"
        val json = buildJsonObject {
            put("snowflake", value)
        }

        val container = Json.decodeFromJsonElement<SnowflakeContainer>(json)
        assertEquals(value, container.snowflake.toString())
    }

    @Test
    fun `Deserialization of Snowflake as large String completes successfully`() {
        val value = Snowflake.validValues.last.toString()
        val json = buildJsonObject {
            put("snowflake", value)
        }

        val container = Json.decodeFromJsonElement<SnowflakeContainer>(json)
        assertEquals(value, container.snowflake.toString())
    }

    @Test
    fun `Deserialization of Snowflake as Number completes successfully`() {
        val value = 1337L
        val json = buildJsonObject {
            put("snowflake", value)
        }

        val container = Json.decodeFromJsonElement<SnowflakeContainer>(json)
        assertEquals(value.toULong(), container.snowflake.value)
    }

    @Test
    fun `Deserialization of Snowflake as large Number completes successfully`() {
        // ULong is inline class and therefore cannot extend abstract class Number but put only takes Number
        val value = Snowflake.validValues.last.toNumber()
        val json = buildJsonObject {
            put("snowflake", value)
        }

        val container = Json.decodeFromJsonElement<SnowflakeContainer>(json)
        assertEquals(value.value, container.snowflake.value)
    }

    @Test
    fun `Reserialization of Snowflake completes successfully`() {
        val json = buildJsonObject { put("snowflake", 1337L) }

        val container = Json.decodeFromJsonElement<SnowflakeContainer>(json)
        val reDecodedContainer = Json.decodeFromString<SnowflakeContainer>(Json.encodeToString(container))
        assertEquals(container, reDecodedContainer)
    }
}
