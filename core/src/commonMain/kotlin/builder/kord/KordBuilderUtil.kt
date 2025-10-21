package dev.kord.core.builder.kord

import dev.kord.common.annotation.KordInternal
import dev.kord.common.entity.Snowflake
import dev.kord.common.http.httpEngine
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.json.Json

internal fun HttpClientConfig<*>.defaultConfig() {
    expectSuccess = false

    val json = Json {
        encodeDefaults = false
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    install(ContentNegotiation) {
        json(json)
    }
    install(WebSockets)
}

/** @suppress */
@KordInternal
public fun HttpClient?.configure(): HttpClient {
    if (this != null) return this.config {
        defaultConfig()
    }

    return HttpClient(httpEngine()) {
        defaultConfig()
    }
}

/** @suppress */
@KordInternal
public fun getBotIdFromToken(token: String): Snowflake = try {
    Snowflake(token.substringBefore('.').decodeBase64String())
} catch (exception: IllegalArgumentException) {
    throw IllegalArgumentException("Malformed bot token: '$token'. Make sure that your token is correct.")
}
