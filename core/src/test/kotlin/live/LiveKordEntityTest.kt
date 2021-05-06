package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.event.channel.CategoryCreateEvent
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.AbstractLiveKordEntity
import dev.kord.core.live.on
import dev.kord.gateway.GuildBanAdd
import dev.kord.gateway.GuildDelete
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
class LiveKordEntityTest : AbstractLiveEntityTest<LiveKordEntityTest.LiveEntityMock>() {

    @OptIn(KordPreview::class)
    inner class LiveEntityMock(override val kord: Kord) :
        AbstractLiveKordEntity(Dispatchers.Default, kord.coroutineContext.job) {

        var counterUpdate: Int = 0

        override val id: Snowflake = Snowflake(1)

        override fun filter(event: Event): Boolean = event is BanAddEvent

        override fun update(event: Event) {
            if (event is BanAddEvent) {
                counterUpdate++
            }
        }
    }

    @BeforeTest
    fun onBefore() {
        live = LiveEntityMock(kord)
    }

    @Test
    fun `Shutdown entity cancel the lifecycle`() = runBlocking {
        assertTrue(live.isActive)
        live.shutdown()
        assertFalse(live.isActive)
    }

    @Test
    fun `Children job are cancelled when the live entity is shutdown`() = runBlocking {
        val job = live.on<Event> { }
        assertTrue(job.isActive)

        live.shutdown()
        assertTrue(job.isCancelled)
        assertFalse(live.isActive)
    }

    @Test
    fun `Shutdown entity without listening events`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }
            live.shutdown()
        }
    }

    @Test
    fun `Replace the shutdown action with null value`() {
        live.onShutdown {
            error("Must not be executed")
        }
        live.onShutdown(null)
        live.shutdown()
    }

    @Test
    fun `Replace the shutdown action with another action`() {
        live.onShutdown {
            error("Must not be executed")
        }
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }
            live.shutdown()
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
                countDown()
            }
            live.shutdown()
        }
    }

    @Test
    fun `Check if a the filter and update are executed`() = runBlocking {
        assertEquals(0, live.counterUpdate)

        countdownContext(1) {
            live.on<BanAddEvent> {
                countDown()
            }

            live.on<GuildDeleteEvent> {
                error("Must not be executed")
            }

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

            sendEvent(eventGuildBan)

            val eventGuildDelete = GuildDelete(
                DiscordUnavailableGuild(
                    id = randomId()
                ),
                0
            )

            sendEvent(eventGuildDelete)
        }

        // Two expected because there is 2 jobs.
        // So when the CategoryCreateEvent is called
        // each job process the update method
        assertEquals(2, live.counterUpdate)
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