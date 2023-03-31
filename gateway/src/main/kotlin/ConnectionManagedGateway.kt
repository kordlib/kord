package dev.kord.gateway

import dev.kord.common.entity.optional.optional
import dev.kord.common.entity.optional.optionalInt
import dev.kord.gateway.connection.GatewayConnection
import dev.kord.gateway.connection.GatewayConnection.CloseReason
import dev.kord.gateway.connection.GatewayConnection.Session
import dev.kord.gateway.connection.GatewayConnectionProvider
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import java.net.URI
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

internal class ConnectionManagedGateway(
    private val connectionProvider: GatewayConnectionProvider,
    private val data: DefaultGatewayData
) : BaseGateway() {

    override val dispatcher: CoroutineDispatcher = data.dispatcher
    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcher
    override val ping: MutableStateFlow<Duration?> = MutableStateFlow(null)
    override val events: MutableSharedFlow<Event> = data.eventFlow
    private val atomicConnection: AtomicRef<GatewayConnection?> = atomic(null)
    private val atomicSession: AtomicRef<Session?> = atomic(null)
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun onStart(configuration: GatewayConfiguration) {
        data.reconnectRetry.reset()
        while (data.reconnectRetry.hasNext && state is State.Running) {
            val connection = connectionProvider.provide().also { atomicConnection.value = it }
            val pingForward = connection.ping.onEach { ping.value = it }.launchIn(this)
            val session = atomicSession.value ?: newSessionFromConfig(configuration).also { atomicSession.value = it }
            val connectionData = GatewayConnection.Data(
                shard = configuration.shard,
                uri = URI.create(data.url),
                session = session,
                client = data.client,
                json = jsonParser,
                eventFlow = data.eventFlow,
                sendRateLimiter = data.sendRateLimiter,
                identifyRateLimiter = data.identifyRateLimiter,
                reconnectRetry = data.reconnectRetry
            )
            val closeReason = connection.open(connectionData)
            pingForward.cancel()
            when (closeReason) {
                is CloseReason.ResumableInvalidSession -> {
                    log.trace { "Gateway resumable invalid session." }
                    atomicSession.value = Session.Resumed(closeReason.resume)
                }

                is CloseReason.ResumableReconnect -> {
                    log.trace { "Gateway resumable reconnect." }
                    atomicSession.value = Session.Resumed(closeReason.resume)
                }

                is CloseReason.Manual -> {
                    log.trace { "Gateway connection closed manually." }
                    break
                }

                is CloseReason.Error -> {
                    log.error(closeReason.cause) { "Gateway connection closed with error." }
                    if (closeReason.cause is java.nio.channels.UnresolvedAddressException) {
                        data.eventFlow.emit(Close.Timeout)
                    }
                }

                is CloseReason.Plain -> {
                    val closeReasonCode = GatewayCloseCode.values().find { it.code == closeReason.code }
                    if (closeReasonCode == null || !closeReasonCode.retry) {
                        error("Gateway closed: ${closeReason.code} ${closeReason.message}")
                    }
                    atomicSession.value = if (closeReasonCode.resetSession && closeReason.resume != null) {
                        Session.Resumed(closeReason.resume)
                    } else null
                }

                else -> atomicSession.value = null
            }
        }
    }

    private fun newSessionFromConfig(config: GatewayConfiguration): Session.New {
        val identify = Identify(
            token = config.token,
            properties = IdentifyProperties(os, config.name, config.name),
            compress = false.optional(),
            largeThreshold = config.threshold.optionalInt(),
            shard = config.shard.optional(),
            presence = config.presence,
            intents = config.intents
        )
        return Session.New(identify)
    }

    override suspend fun onStop() {
        useConnection { it.close() }
    }

    override suspend fun onDetach() {
        useConnection { it.close() }
    }

    override suspend fun onSend(command: Command) {
        useConnection { it.send(command) }
    }

    private inline fun useConnection(block: (GatewayConnection) -> Unit) {
        atomicConnection.loop {
            if (it != null) {
                block(it)
                return
            }
        }
    }
}
