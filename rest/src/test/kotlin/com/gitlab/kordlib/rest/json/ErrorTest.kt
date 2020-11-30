package dev.kord.rest.json

import dev.kord.rest.json.response.DiscordErrorResponse
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ErrorTest {
    private val parser = Json {
        encodeDefaults = false
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `correctly serialize error`() {
        val content = file("error")
        val parsed = parser.decodeFromString(DiscordErrorResponse.serializer(), content)
        assertEquals(40001, parsed.code.code)
        assertEquals("Unauthorized", parsed.message)

    }
}
