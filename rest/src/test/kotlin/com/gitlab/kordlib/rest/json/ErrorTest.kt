package com.gitlab.kordlib.rest.json

import com.gitlab.kordlib.rest.json.response.DiscordErrorResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ErrorTest {
    private val parser = Json(JsonConfiguration(encodeDefaults = false, allowStructuredMapKeys = true, ignoreUnknownKeys = true, isLenient = true))
    @Test
    fun `correctly serialize error`() {
        val content = file("error")
        val parsed  = parser.parse(DiscordErrorResponse.serializer(), content)
        assertEquals(40001,parsed.code.code)
        assertEquals("Unauthorized",parsed.message)

    }
}