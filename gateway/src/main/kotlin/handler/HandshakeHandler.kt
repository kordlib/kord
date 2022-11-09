package dev.kord.gateway.handler

import dev.kord.gateway.*
import dev.kord.gateway.retry.Retry
import io.ktor.http.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow

internal class HandshakeHandler(
    flow: Flow<Event>,
    private val initialUrl: Url,
    private val send: suspend (Command) -> Unit,
    private val sequence: Sequence,
    private val reconnectRetry: Retry
) : Handler(flow, "HandshakeHandler") {

    lateinit var configuration: GatewayConfiguration

    // see https://discord.com/developers/docs/topics/gateway#resuming
    internal class ResumeContext(val sessionId: String, val resumeUrl: Url)

    internal val resumeContext = atomic<ResumeContext?>(initial = null)
    val gatewayUrl get() = resumeContext.value?.resumeUrl ?: initialUrl

    val needsIdentifyAndGatewayUrl: Pair<Boolean, Url>
        get() {
            val context = resumeContext.value
            val needsIdentify = context == null
            val gatewayUrl = context?.resumeUrl ?: initialUrl
            return Pair(needsIdentify, gatewayUrl)
        }

    private val resumeOrIdentify
        get() = when (val sessionId = resumeContext.value?.sessionId) {
            null -> configuration.identify
            else -> Resume(configuration.token, sessionId, sequence.value ?: 0)
        }

    override fun start() {
        on<Hello> {
            reconnectRetry.reset() // connected and read without problems, resetting retry counter
            send(resumeOrIdentify)
        }

        on<Ready> { event ->
            // keep custom query params
            val resumeUrl = URLBuilder(event.data.resumeGatewayUrl)
                .apply { parameters.appendMissing(initialUrl.parameters) }
                .build()

            resumeContext.value = ResumeContext(event.data.sessionId, resumeUrl)
        }

        on<Close.SessionReset> {
            resumeContext.value = null
        }
    }
}
