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
    inner class LiveEntityMock(kord: Kord) :
        AbstractLiveKordEntity(kord, Dispatchers.Default) {

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
            live.coroutineContext.job.invokeOnCompletion {
                count()
            }
            live.shutDown()
        }
    }

    @Test
    fun `Check if the filter and update are executed`() {
        // The expected count is 4 because each job will increment the counter.
        // Each job (BanAddEvent, GuildDeleteEvent and initial) will process the update function.
        // Another count is the action for BanAddEvent
        countdownContext(4) {
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