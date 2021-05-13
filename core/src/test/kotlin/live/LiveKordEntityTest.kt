package live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordGuildBan
import dev.kord.common.entity.DiscordUnavailableGuild
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.AbstractLiveKordEntity
import dev.kord.core.live.on
import dev.kord.gateway.GuildBanAdd
import dev.kord.gateway.GuildDelete
import equality.randomId
import kotlinx.coroutines.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
class LiveKordEntityTest : AbstractLiveEntityTest<LiveKordEntityTest.LiveEntityMock>() {

    @Disabled
    inner class LiveEntityMock(override val kord: Kord) :
        AbstractLiveKordEntity(Dispatchers.Default, kord.coroutineContext.job) {

        var counter: CounterAtomicLatch? = null

        override val id: Snowflake = randomId()

        override fun filter(event: Event): Boolean = event is BanAddEvent

        override fun update(event: Event) {
            if (event is BanAddEvent) {
                counter?.count()
            }
        }
    }

    @BeforeTest
    fun onBefore() {
        live = LiveEntityMock(kord)
    }

    @Test
    fun `Shutdown entity cancel the lifecycle`() {
        assertTrue(live.isActive)
        live.shutDown()
        assertFalse(live.isActive)
    }

    @Test
    fun `Children job are cancelled when the live entity is shutdown`() {
        val job = live.on<Event> { }
        assertTrue(job.isActive)

        live.shutDown()
        assertTrue(job.isCancelled)
        assertFalse(live.isActive)
    }

    @Test
    fun `Shutdown entity without listening events`() {
        countdownContext(1) {
            live.onShutdown {
                count()
            }
            live.shutDown()
        }
    }

    @Test
    fun `Replace the shutdown action with null value`() {
        live.onShutdown {
            error("Must not be executed")
        }
        live.onShutdown(null)
        live.shutDown()
    }

    @Test
    fun `Replace the shutdown action with another action`() {
        live.onShutdown {
            error("Must not be executed")
        }
        countdownContext(1) {
            live.onShutdown {
                count()
            }
            live.shutDown()
        }
    }

    @Test
    fun `Replace the shutdown action with null value and another action`() {
        live.onShutdown {
            error("Must not be executed")
        }
        live.onShutdown(null)
        countdownContext(1) {
            live.onShutdown {
                count()
            }
            live.shutDown()
        }
    }

    @Test
    fun `Check if the filter and update are executed`() {
        // Second countdown in the live entity to check
        // if the method update is called
        countdownContext(3) {
            live.counter = this

            live.on<BanAddEvent> {
                count()
            }

            live.on<GuildDeleteEvent> {
                error("Must not be executed")
            }

            // Wait that the 2 jobs are ready to listen the next events.
            // Without the delay, the success of the test is uncertain.
            delay(50)

            val eventGuildBan = GuildBanAdd(
                DiscordGuildBan(
                    guildId = guildId.asString,
                    user = DiscordUser(
                        id = randomId(),
                        username = "",
                        discriminator = "",
                        avatar = null
                    )
                ),
                0
            )

            sendEventAndWait(eventGuildBan)

            val eventGuildDelete = GuildDelete(
                DiscordUnavailableGuild(
                    id = guildId
                ),
                0
            )

            sendEvent(eventGuildDelete)
        }
    }

    @Test
    fun `Check the entity is cancelled when kord is cancelled`() = runBlocking {
        val job = live.on<BanAddEvent> {
            error("Never called")
        }

        live.onShutdown {
            error("Never called")
        }

        assertTrue(kord.isActive)
        assertTrue(live.isActive)
        assertTrue(job.isActive)

        kord.logout()
        kord.shutdown()

        assertFalse(kord.isActive)
        assertFalse(live.isActive)
        assertFalse(job.isActive)

        kord = createKord()
    }
}