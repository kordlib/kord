package dev.kord.rest.json

import dev.kord.rest.json.response.DiscordErrorResponse
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

@IgnoreOnSimulatorPlatforms
class ErrorTest {
    private val parser = Json {
        encodeDefaults = false
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    @JsName("test1")
    fun `correctly serialize error`() = runTest {
        val content = file("error")
        val parsed = parser.decodeFromString(DiscordErrorResponse.serializer(), content)
        assertEquals(40001, parsed.code.code)
        assertEquals("Unauthorized", parsed.message)

    }
}
