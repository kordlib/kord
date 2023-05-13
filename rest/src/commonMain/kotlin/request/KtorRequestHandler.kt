package dev.kord.rest.request

import dev.kord.common.http.HttpEngine
import dev.kord.rest.json.response.DiscordErrorResponse
import dev.kord.rest.ratelimit.*
import dev.kord.rest.route.optional
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.TextContent
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import mu.KotlinLogging

internal val jsonDefault = Json {
    encodeDefaults = false
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
    isLenient = true
}

public fun RequestResponse.Companion.from(response: HttpResponse, clock: Clock): RequestResponse {
    val bucket = response.bucket
    val rateLimit = run {
        val total = Total(response.rateLimitTotal ?: return@run null)
        val remaining = Remaining(response.rateLimitRemaining ?: return@run null)
        RateLimit(total, remaining)
    }

    val reset = Reset(response.channelResetPoint(clock))

    return when {
        response.isGlobalRateLimit -> RequestResponse.GlobalRateLimit(bucket, rateLimit, reset)
        response.isRateLimit -> RequestResponse.BucketRateLimit(
            bucket
                ?: BucketKey("missing"), rateLimit, reset
        )
        response.isError -> RequestResponse.Error
        else -> RequestResponse.Accepted(bucket, rateLimit, reset)
    }
}
