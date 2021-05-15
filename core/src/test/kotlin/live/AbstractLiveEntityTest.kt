package live

import dev.kord.cache.api.DataCache
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.builder.kord.Shards
import dev.kord.core.gateway.MasterGateway
import dev.kord.core.live.AbstractLiveKordEntity
import dev.kord.core.on
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.*
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
import org.junit.jupiter.api.Timeout
import java.time.Clock
import java.util.*
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

@OptIn(KordPreview::class)
abstract class AbstractLiveEntityTest<LIVE : AbstractLiveKordEntity> {

    inner class GatewayMock : Gateway {
        override val coroutineContext: CoroutineContext = EmptyCoroutineContext + SupervisorJob()

        @OptIn(FlowPreview::class)
        override val events: MutableSharedFlow<Event> = MutableSharedFlow()

        override val ping: StateFlow<Duration?> = MutableStateFlow(null)

        override suspend fun detach() {}

        override suspend fun send(command: Command) {}

        override suspend fun start(configuration: GatewayConfiguration) {}

        override suspend fun stop() {}
    }

    inner class EventQueueManager(private val kord: Kord) {

        private val queue = LinkedList<suspend () -> Unit>()
        private var job: Job? = null

        fun add(block: suspend () -> Unit) {
            queue.add(block)
        }

        private suspend fun pollAndInvoke() {
            queue.poll().invoke()
            if (queue.isEmpty()) {
                job?.cancelAndJoin()
            }
        }

        suspend fun start() {
            job = kord.on<dev.kord.core.event.Event> {
                pollAndInvoke()
            }
            delay(50)
            pollAndInvoke()
        }
    }

    inner class CounterAtomicLatch(count: Int) {

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

    lateinit var kord: Kord

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
            resources = ClientResources("token", Shards(1), HttpClient(), EntitySupplyStrategy.cache, Intents.none),
            cache = DataCache.none(),
            MasterGateway(mapOf(0 to gateway)),
            RestClient(KtorRequestHandler("token", clock = Clock.systemUTC())),
            randomId(),
            MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
            Dispatchers.Default
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

    suspend inline fun sendEventValidAndRandomId(validId: Snowflake, crossinline builderEvent: (Snowflake) -> Event) {
        EventQueueManager(kord).apply {
            add {
                sendEvent(builderEvent(randomId()))
            }
            add {
                sendEvent(builderEvent(validId))
            }
            start()
        }
    }

    suspend inline fun sendEventValidAndRandomIdCheckLiveActive(
        validId: Snowflake,
        crossinline builderEvent: (Snowflake) -> Event
    ) {
        EventQueueManager(kord).apply {
            add {
                sendEvent(builderEvent(randomId()))
            }
            // When the wrong event is received.
            add {
                assertTrue { live.isActive }
                sendEvent(builderEvent(validId))
            }
            // When the good event is received.
            add {
                assertFalse { live.isActive }
            }
            start()
        }
    }

    suspend fun sendEvent(event: Event) = gateway.events.emit(event)
}