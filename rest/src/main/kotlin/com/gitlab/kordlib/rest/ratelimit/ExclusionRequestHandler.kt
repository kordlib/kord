package com.gitlab.kordlib.rest.ratelimit

import com.gitlab.kordlib.common.ratelimit.BucketRateLimiter
import com.gitlab.kordlib.rest.request.*
import com.gitlab.kordlib.rest.route.Route
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.readText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.takeFrom
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KotlinLogging
import java.time.Clock
import kotlin.time.minutes

private val logger = KotlinLogging.logger {}

typealias Bucket = String

@Suppress("EXPERIMENTAL_API_USAGE")
private val parser = Json(JsonConfiguration(encodeDefaults = false, strictMode = false))


class ExclusionRequestHandler(private val client: HttpClient, private val clock: Clock = Clock.systemUTC()) : RequestHandler {

    constructor(token: String) : this(HttpClient(CIO) {
        defaultRequest {
            header("Authorization", "Bot $token")
        }
    })

    private var globalSuspensionPoint = 0L

    private val routeSuspensionPoints = mutableMapOf<RequestIdentifier, Long>()

    private val buckets = mutableMapOf<Route<*>, Bucket>()
    private val bucketSuspensionPoints = mutableMapOf<Bucket, Long>()

    private val mutex = Mutex()

    private val autoBanRateLimiter = BucketRateLimiter(25000, 10.minutes)

    override tailrec suspend fun <B : Any, R> handle(request: Request<B, R>): R {

        val response = mutex.withLock {
            suspendFor(request)

            logger.trace { "REQUEST: ${request.logString}" }
            val response = client.request<HttpStatement> {
                method = request.route.method
                headers.append("X-RateLimit-Precision", "millisecond")
                headers.appendAll(request.headers)

                url {
                    url.takeFrom(Route.baseUrl)
                    encodedPath += request.path
                    parameters.appendAll(request.parameters)
                }

                request.body?.let {
                    when (request) {
                        is MultipartRequest<*, *> -> {
                            headers.append("payload_json", parser.stringify(it.strategy as SerializationStrategy<Any>, it.body))
                            this.body = MultiPartFormDataContent(request.data)
                        }

                        is JsonRequest<*, *> -> {
                            val json = parser.stringify(it.strategy as SerializationStrategy<Any>, it.body)
                            this.body = TextContent(json, ContentType.Application.Json)
                        }
                    }
                }

            }.execute()

            logger.trace { response.logString }

            if (response.isGlobalRateLimit) {
                logger.trace { "GLOBAL RATE LIMIT UNTIL ${response.globalSuspensionPoint(clock)}: ${request.logString}" }
                globalSuspensionPoint = response.globalSuspensionPoint(clock)
            }

            if (response.isChannelRateLimit) {
                logger.trace { "ROUTE RATE LIMIT UNTIL ${response.channelSuspensionPoint}: ${request.logString}" }
                routeSuspensionPoints[request.identifier] = response.channelSuspensionPoint

                val bucket = response.bucket
                if (bucket != null) {
                    buckets[request.route] = bucket
                    bucketSuspensionPoints[bucket] = response.channelSuspensionPoint
                }
            }

            response
        }

        if (response.isRateLimit) {
            autoBanRateLimiter.consume()
            return handle(request)
        }

        if (response.isErrorWithRateLimit) {
            autoBanRateLimiter.consume()
        }

        if (response.isError) {
            throw RequestException(response, response.errorString())
        }

        return parser.parse(request.route.strategy, response.readText())
    }

    private suspend fun suspendFor(request: Request<*, *>) {
        delay(globalSuspensionPoint - clock.millis())
        globalSuspensionPoint = 0

        val key = request.identifier
        val routeSuspensionPoint = routeSuspensionPoints[key]

        if (routeSuspensionPoint != null) {
            delay(routeSuspensionPoint - clock.millis())
            routeSuspensionPoints.remove(key)
        }

        val bucketSuspensionPoint = getBucketSuspensionPointFor(request)
        if (bucketSuspensionPoint != null) {
            delay(bucketSuspensionPoint - clock.millis())
            removeBucketSuspensionPointFor(request)
        }

    }

    private fun getBucketSuspensionPointFor(request: Request<*, *>): Long? {
        val bucket = buckets[request.route] ?: return null
        return bucketSuspensionPoints[bucket]
    }

    private fun removeBucketSuspensionPointFor(request: Request<*, *>) {
        val bucket = buckets[request.route] ?: return
        bucketSuspensionPoints.remove(bucket)
    }

    private companion object {
        const val rateLimitGlobalHeader = "X-RateLimit-Global"
        const val retryAfterHeader = "Retry-After"
        const val rateLimitRemainingHeader = "X-RateLimit-Remaining"
        const val resetTimeHeader = "X-RateLimit-Reset"
        const val bucketRateLimitKey = "X-RateLimit-Bucket"

        /**
         * The unix time (in ms) when the rate limit for this endpoint gets reset
         */
        val HttpResponse.channelSuspensionPoint: Long
            get() {
                val unixSeconds = headers[resetTimeHeader]?.toDouble() ?: return 0
                return (unixSeconds * 1000).toLong()
            }

        val HttpResponse.isRateLimit get() = status.value == 429
        val HttpResponse.isError get() = status.value in 400 until 600
        val HttpResponse.isErrorWithRateLimit get() = status.value == 403 || status.value == 401
        val HttpResponse.isGlobalRateLimit get() = headers[rateLimitGlobalHeader]?.toBoolean() == true
        val HttpResponse.isChannelRateLimit get() = headers[rateLimitRemainingHeader]?.toIntOrNull() == 0
        val HttpResponse.bucket: Bucket? get() = headers[bucketRateLimitKey]

        /**
         * The unix time (in ms) when the global rate limit gets reset
         */
        fun HttpResponse.globalSuspensionPoint(clock: Clock): Long {
            val msWait = headers[retryAfterHeader]?.toLong() ?: return 0
            return msWait + clock.millis()
        }

        val HttpResponse.logString get() = "$status: ${call.request.method.value} ${call.request.url}"

        suspend fun HttpResponse.errorString(): String {
            val message = String(this.readBytes())
            return if (message.isBlank()) logString
            else "$logString $message"
        }
    }
}

