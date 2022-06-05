package dev.kord.core.builder.kord

import dev.kord.common.entity.Snowflake
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.json.Json

internal fun HttpClientConfig<*>.defaultConfig() {
    expectSuccess = false

    install(ContentNegotiation) {
        json()
    }
    install(WebSockets)
}

internal fun HttpClient?.configure(): HttpClient {
    if (this != null) return this.config {
        defaultConfig()
    }

    val json = Json {
        encodeDefaults = false
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    return HttpClient(CIO) {
        defaultConfig()
        install(ContentNegotiation) {
            json(json)
        }
    }
}


internal fun getBotIdFromToken(token: String) = try {
    Snowflake(token.substringBefore('.').decodeBase64String())
} catch (exception: IllegalArgumentException) {
    throw IllegalArgumentException("Malformed bot token: '$token'. Make sure that your token is correct.")
}
