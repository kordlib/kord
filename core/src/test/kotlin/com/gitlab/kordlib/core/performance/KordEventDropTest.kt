package com.gitlab.kordlib.core.performance

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.core.ClientResources
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.guild.GuildCreateEvent
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.core.on
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.gateway.*
import com.gitlab.kordlib.rest.request.KtorRequestHandler
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.time.Clock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.minutes

class KordEventDropTest {

    object SpammyGateway : Gateway {
        override val coroutineContext: CoroutineContext = EmptyCoroutineContext + SupervisorJob()

        @OptIn(FlowPreview::class)
        override val events: MutableSharedFlow<Event> = MutableSharedFlow()

        override val ping: StateFlow<Duration?> = MutableStateFlow(null)

        override suspend fun detach() {}

        override suspend fun send(command: Command) {}

        override suspend fun start(configuration: GatewayConfiguration) {}

        override suspend fun stop() {}
    }

    val kord = Kord(
            resources = ClientResources("token", 1, HttpClient(), EntitySupplyStrategy.cache, Intents.none),
            cache = DataCache.none(),
            MasterGateway(mapOf(0 to SpammyGateway)),
            RestClient(KtorRequestHandler("token", clock = Clock.systemUTC())),
            Snowflake("420"),
            MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
            Dispatchers.Default
    )

    @Test
    fun `hammering the gateway does not drop core events`() = runBlocking {
        val amount = 1_000

        val event = GuildCreate(
                DiscordGuild(
                        Snowflake("1337"),
                        "discord guild",
                        afkTimeout = 0,
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
                        publicUpdatesChannelId = null
                ), 0)

        val counter = AtomicInteger(0)
        val countdown = CountDownLatch(amount)
        kord.on<GuildCreateEvent> {
            counter.incrementAndGet()
            countdown.countDown()
        }

        repeat(amount) {
            SpammyGateway.events.emit(event)
        }

        withTimeout(1.minutes) {
            countdown.await()
        }
        assertEquals(amount, counter.get())
    }

}
