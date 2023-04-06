package dev.kord.rest.request

import dev.kord.rest.json.response.GatewayResponse
import dev.kord.rest.route.Route
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test

expect class StackTraceElement
expect fun currentThreadStackTrace(): StackTraceElement
internal expect fun RecoveredStackTrace.validate(expected: StackTraceElement)

class StackTraceRecoveryTest {

    @Test
    @JsName("test1")
    fun `test stack trace recovery`() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel.Empty,
                status = HttpStatusCode.NotFound
            )
        }

        val client = HttpClient(mockEngine)
        val handler = KtorRequestHandler(client = client, token = "")
            .withStackTraceRecovery()

        val request = JsonRequest<GatewayResponse, GatewayResponse>(
            Route.GatewayGet, // The mock engine will 404 for any request, so we just use an endpoint without params
            emptyMap(),
            StringValues.Empty,
            StringValues.Empty,
            null
        )

        val stackTrace = currentThreadStackTrace()
        try {
            handler.handle(request)
        } catch (e: Throwable) {
            e.printStackTrace()

            val recovered = e.suppressedExceptions.first { it is RecoveredStackTrace } as RecoveredStackTrace
            recovered.printStackTrace()
            recovered.validate(stackTrace)
        }
    }
}
