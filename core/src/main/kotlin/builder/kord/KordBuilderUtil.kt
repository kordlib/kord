package dev.kord.core.builder.kord

import dev.kord.common.entity.Snowflake
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import java.util.*

@OptIn(KtorExperimentalAPI::class)
internal fun HttpClientConfig<*>.defaultConfig(token: String) {
    expectSuccess = false
    defaultRequest {
        header("Authorization", "Bot $token")
    }

    install(JsonFeature)
    install(WebSockets)
}

@OptIn(KtorExperimentalAPI::class)
internal fun HttpClient?.configure(token: String): HttpClient {
    if (this != null) return this.config {
        defaultConfig(token)
    }

    val json = Json {
        encodeDefaults = false
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    return HttpClient(CIO) {
        defaultConfig(token)
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }
    }
}


internal fun getBotIdFromToken(token: String): Snowflake {
    try {
        val bytes = Base64.getDecoder().decode(token.split(""".""").first())
        return Snowflake(String(bytes))
    } catch (exception: IllegalArgumentException) {
        throw IllegalArgumentException("Malformed bot token: '$token'. Make sure that your token is correct.")
    }
}

