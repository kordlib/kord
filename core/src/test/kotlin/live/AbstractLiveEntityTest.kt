package live

import dev.kord.cache.api.DataCache
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createTextChannel
import dev.kord.core.behavior.channel.createVoiceChannel
import dev.kord.core.behavior.createCategory
import dev.kord.core.builder.kord.KordBuilder
import dev.kord.core.builder.kord.Shards
import dev.kord.core.cache.KordCacheBuilder
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.gateway.MasterGateway
import dev.kord.core.live.AbstractLiveKordEntity
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.*
import dev.kord.rest.builder.channel.CategoryCreateBuilder
import dev.kord.rest.builder.channel.TextChannelCreateBuilder
import dev.kord.rest.builder.channel.VoiceChannelCreateBuilder
import dev.kord.rest.builder.guild.GuildCreateBuilder
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.time.Clock
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.time.Duration

@OptIn(PrivilegedIntent::class, KordPreview::class)
abstract class AbstractLiveEntityTest<LIVE : AbstractLiveKordEntity> {

    object GatewayMock : Gateway {
        override val coroutineContext: CoroutineContext = EmptyCoroutineContext + SupervisorJob()

        @OptIn(FlowPreview::class)
        override val events: MutableSharedFlow<Event> = MutableSharedFlow()

        override val ping: StateFlow<Duration?> = MutableStateFlow(null)

        override suspend fun detach() {}

        override suspend fun send(command: Command) {}

        override suspend fun start(configuration: GatewayConfiguration) {}

        override suspend fun stop() {}
    }

    protected lateinit var kord: Kord

    protected lateinit var live: LIVE

    @BeforeAll
    open fun onBeforeAll() = runBlocking {
        kord = createKord()
    }

    @AfterAll
    open fun onAfterAll() = runBlocking<Unit> {
        if (kord.isActive) {
            kord.logout()
            kord.shutdown()
        }
    }

    @AfterTest
    open fun onAfter() {
        if (this::live.isInitialized && live.isActive) {
            live.shutdown()
        }
    }

    protected open fun createKord(): Kord = Kord(
        resources = ClientResources("token", Shards(1), HttpClient(), EntitySupplyStrategy.cache, Intents.none),
        cache = DataCache.none(),
        MasterGateway(mapOf(0 to GatewayMock)),
        RestClient(KtorRequestHandler("token", clock = Clock.systemUTC())),
        Snowflake("420"),
        MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
        Dispatchers.Default
    )

    protected inline fun countdownContext(
        count: Int,
        expectedCount: Long = 0,
        waitMs: Long = 5000,
        crossinline action: suspend CountDownLatch.() -> Unit
    ) = runBlocking {
        val countdown = CountDownLatch(count)

        action(countdown)

        countdown.await(waitMs, TimeUnit.MILLISECONDS)
        assertEquals(expectedCount, countdown.count)
    }

    protected suspend fun sendEvent(event: Event) = GatewayMock.events.emit(event)
}