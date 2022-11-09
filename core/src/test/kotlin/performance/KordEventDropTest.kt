package dev.kord.core.performance

import dev.kord.cache.api.DataCache
import dev.kord.common.entity.*
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.on
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.*
import dev.kord.gateway.builder.Shards
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class KordEventDropTest {

    object SpammyGateway : Gateway {

        override val coroutineContext: CoroutineContext = SupervisorJob() + EmptyCoroutineContext

        override val events: MutableSharedFlow<Event> = MutableSharedFlow()

        override val ping: StateFlow<Duration?> = MutableStateFlow(null)

        override suspend fun detach() {}

        override suspend fun send(command: Command) {}

        override suspend fun start(configuration: GatewayConfiguration) {}

        override suspend fun stop(closeReason: WebSocketCloseReason): GatewayResumeConfiguration { error("Can't stop this!") }

        override suspend fun resume(configuration: GatewayResumeConfiguration) {}
    }

    val kord = Kord(
        resources = ClientResources("token", Snowflake(0u), Shards(1), maxConcurrency = 1, HttpClient(), EntitySupplyStrategy.cache),
        cache = DataCache.none(),
        DefaultMasterGateway(mapOf(0 to SpammyGateway)),
        RestClient(KtorRequestHandler("token", clock = Clock.System)),
        Snowflake("420"),
        MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
        Dispatchers.Default,
        DefaultGatewayEventInterceptor(),
    )

    @Test
    fun `hammering the gateway does not drop core events`() = runBlocking {
        val amount = 1_000

        val event = GuildCreate(
            DiscordGuild(
                Snowflake("1337"),
                "discord guild",
                afkTimeout = 0.seconds,
                defaultMessageNotifications = DefaultMessageNotificationLevel.AllMessages,
                emojis = emptyList(),
                explicitContentFilter = ExplicitContentFilter.AllMembers,
                features = emptyList(),
                mfaLevel = MFALevel.Elevated,
                ownerId = Snowflake("123"),
                preferredLocale = "en",
                description = "A not really real guild",
                premiumTier = PremiumTier.None,
                region = "idk",
                roles = emptyList(),
                verificationLevel = VerificationLevel.High,
                icon = null,
                afkChannelId = null,
                applicationId = null,
                systemChannelFlags = SystemChannelFlags(0),
                systemChannelId = null,
                rulesChannelId = null,
                vanityUrlCode = null,
                banner = null,
                publicUpdatesChannelId = null,
                nsfwLevel = NsfwLevel.Default,
                premiumProgressBarEnabled = false
            ), 0
        )

        val counter = AtomicInteger(0)
        val countdown = CountDownLatch(amount)
        kord.on<GuildCreateEvent> {
            counter.incrementAndGet()
            countdown.countDown()
        }

        repeat(amount) {
            SpammyGateway.events.emit(event)
        }

        withTimeout(1.minutes.inWholeMilliseconds) {
            countdown.await()
        }
        assertEquals(amount, counter.get())
    }

}
