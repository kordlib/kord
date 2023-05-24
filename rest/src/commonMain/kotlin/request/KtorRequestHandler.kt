package dev.kord.rest.request

import dev.kord.rest.ratelimit.*
import io.ktor.client.statement.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

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
