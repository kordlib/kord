package dev.kord.core.builder.kord

import dev.kord.common.annotation.KordInternal
import dev.kord.common.entity.Snowflake
import dev.kord.common.http.HttpEngine
import io.ktor.client.*
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

/** @suppress */
@KordInternal
public fun HttpClient?.configure(): HttpClient {
    if (this != null) return this.config {
        defaultConfig()
    }

    val json = Json {
        encodeDefaults = false
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    return HttpClient(HttpEngine) {
        defaultConfig()
        install(ContentNegotiation) {
            json(json)
        }
    }
}

/** @suppress */
@KordInternal
public fun getBotIdFromToken(token: String): Snowflake = try {
    Snowflake(token.substringBefore('.').decodeBase64String())
} catch (exception: IllegalArgumentException) {
    throw IllegalArgumentException("Malformed bot token: '$token'. Make sure that your token is correct.")
}
