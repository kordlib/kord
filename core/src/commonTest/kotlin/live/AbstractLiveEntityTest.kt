package dev.kord.core.live

import dev.kord.cache.api.DataCache
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.randomId
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Command
import dev.kord.gateway.Event
import dev.kord.gateway.Gateway
import dev.kord.gateway.GatewayConfiguration
import dev.kord.gateway.builder.Shards
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

abstract class AbstractLiveEntityTest<LIVE : AbstractLiveKordEntity> {

    companion object {
        const val DELAY_TIME = 400L
    }

    class GatewayMock : Gateway {
        override val coroutineContext: CoroutineContext = EmptyCoroutineContext + SupervisorJob()

        override val events: MutableSharedFlow<Event> = MutableSharedFlow()

        override val ping: StateFlow<Duration?> = MutableStateFlow(null)

        override suspend fun detach() {}

        override suspend fun send(command: Command) {}

        override suspend fun start(configuration: GatewayConfiguration) {}

        override suspend fun stop() {}
    }

    class CounterAtomicLatch {
        private val channel = Channel<Unit>()

        private val received = atomic(0)
        val atomicCount by received

        suspend fun count() {
            channel.send(Unit)
        }

        suspend fun await(duration: Duration) = withTimeout(duration) {
            for(unit in channel) { received.incrementAndGet() }
        }
    }

    private lateinit var gateway: GatewayMock

    protected lateinit var kord: Kord

    protected var guildId: Snowflake = Snowflake.min

    lateinit var live: LIVE

    @BeforeTest
    open fun onBeforeAll() = runTest {
        kord = createKord()
        guildId = randomId()
    }

    @AfterTest
    open fun onAfterAll() = runTest {
        if (::live.isInitialized && live.isActive) {
            live.shutDown()
        }
        if (kord.isActive) {
            kord.logout()
            kord.shutdown()
        }
    }

    protected open fun createKord(): Kord {
        gateway = GatewayMock()
        return Kord(
            resources = ClientResources("token", Snowflake(0u), Shards(1), maxConcurrency = 1, HttpClient(), EntitySupplyStrategy.cache),
            cache = DataCache.none(),
            DefaultMasterGateway(mapOf(0 to gateway)),
            RestClient(KtorRequestHandler(token = "token")),
            randomId(),
            MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
            Dispatchers.Default,
            DefaultGatewayEventInterceptor(),
        )
    }

    protected inline fun countdownContext(
        expectedCount: Int,
        wait: Duration = 5000.milliseconds,
        crossinline action: suspend CounterAtomicLatch.() -> Unit
    ) = runTest {
        val counter = CounterAtomicLatch()

        action(counter)

        counter.await(wait)
        assertEquals(expectedCount, counter.atomicCount)
    }

    suspend inline fun sendEventValidAndRandomId(validId: Snowflake, builderEvent: (Snowflake) -> Event) {
        sendEventAndWait(builderEvent(randomId()))
        sendEvent(builderEvent(validId))
    }

    suspend inline fun sendEventValidAndRandomIdCheckLiveActive(
        validId: Snowflake,
        builderEvent: (Snowflake) -> Event
    ) {
        sendEventAndWait(builderEvent(randomId()))
        assertTrue { live.isActive }
        sendEventAndWait(builderEvent(validId))
        assertFalse { live.isActive }
    }

    suspend fun sendEventAndWait(event: Event, delayMs: Long = DELAY_TIME) {
        sendEvent(event)
        // Let time to receive event from the flow before the next action.
        delay(delayMs)
    }

    suspend fun sendEvent(event: Event) = gateway.events.emit(event)
}
