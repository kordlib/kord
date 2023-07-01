package dev.kord.core.regression

import dev.kord.gateway.Command
import dev.kord.gateway.Event
import dev.kord.gateway.Gateway
import dev.kord.gateway.GatewayConfiguration
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration


private val parser = Json {
    encodeDefaults = false
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
    isLenient = true
}

object FakeGateway : Gateway {

    private val deferred = CompletableDeferred<Unit>()

    override val events: SharedFlow<Event> = MutableSharedFlow()

    override val ping: StateFlow<Duration?> = MutableStateFlow(null)

    override suspend fun detach() {}

    override suspend fun send(command: Command) {}
    override suspend fun start(configuration: GatewayConfiguration) {
        deferred.await()
    }

    override suspend fun stop() {
        deferred.complete(Unit)
    }

    override val coroutineContext: CoroutineContext = SupervisorJob() + EmptyCoroutineContext
}
