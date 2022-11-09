package live

import dev.kord.cache.api.DataCache
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.live.AbstractLiveKordEntity
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.*
import dev.kord.gateway.builder.Shards
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import equality.randomId
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration

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

        override suspend fun stop(closeReason: WebSocketCloseReason): GatewayResumeConfiguration { error("Can't stop this!") }

        override suspend fun resume(configuration: GatewayResumeConfiguration) {}
    }

    class CounterAtomicLatch(count: Int) {

        private val countdown = CountDownLatch(count)
        val latchCount get() = countdown.count

        private val counter = AtomicInteger(0)
        val atomicCount get() = counter.get()

        fun count() {
            counter.incrementAndGet()
            countdown.countDown()
        }

        fun await(timeout: Long, unit: TimeUnit) = countdown.await(timeout, unit)
    }

    private lateinit var gateway: GatewayMock

    protected lateinit var kord: Kord

    protected lateinit var guildId: Snowflake

    lateinit var live: LIVE

    @BeforeAll
    open fun onBeforeAll() = runBlocking {
        kord = createKord()
        guildId = randomId()
    }

    @AfterAll
    open fun onAfterAll() = runBlocking {
        if (kord.isActive) {
            kord.logout()
            kord.shutdown()
        }
    }

    @AfterTest
    open fun onAfter() {
        if (this::live.isInitialized && live.isActive) {
            live.shutDown()
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
        waitMs: Long = 5000,
        crossinline action: suspend CounterAtomicLatch.() -> Unit
    ) = runBlocking {
        val counter = CounterAtomicLatch(expectedCount)

        action(counter)

        counter.await(waitMs, TimeUnit.MILLISECONDS)
        assertEquals(0, counter.latchCount)
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
