package live

import dev.kord.common.entity.*
import dev.kord.core.Kord
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.Event
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.live.AbstractLiveKordEntity
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.live.on
import dev.kord.gateway.GuildBanAdd
import dev.kord.gateway.GuildDelete
import dev.kord.gateway.MessageReactionAdd
import equality.randomId
import kotlinx.coroutines.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
class LiveKordEntityTest : AbstractLiveEntityTest<LiveKordEntityTest.LiveEntityMock>() {

    companion object {
        private const val REASON_SHUTDOWN = "The live entity mock is shut down"
    }

    @Disabled
    inner class LiveEntityMock(kord: Kord) :
        AbstractLiveKordEntity(kord) {

        var counter: CounterAtomicLatch? = null

        override val id: Snowflake = randomId()

        override fun filter(event: Event): Boolean = when (event) {
            is BanAddEvent -> true
            is ReactionAddEvent -> true
            else -> false
        }

        override fun update(event: Event) {
            when (event) {
                is BanAddEvent -> counter?.count()
                is ReactionAddEvent -> shutDown(LiveCancellationException(event, REASON_SHUTDOWN))
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
    fun `Children job are cancelled when the live entity is shut down`() {
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
    fun `Entity can retrieve the event causing the completion`() {
        countdownContext(1, 10000) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as ReactionAddEvent
                assertEquals(emojiExpected, event.emoji)
                assertEquals(REASON_SHUTDOWN, it.message)
                count()
            }

            sendEvent(
                MessageReactionAdd(
                    MessageReactionAddData(
                        messageId = randomId(),
                        channelId = randomId(),
                        userId = randomId(),
                        emoji = DiscordPartialEmoji(null, emojiExpected.name)
                    ),
                    0
                )
            )
        }
    }

    @Test
    fun `Check if the filter and update are executed`() {
        // The update function is called once time (increment counter) and BanAddEvent is managed (increment counter)
        countdownContext(2) {
            live.counter = this

            live.on<BanAddEvent> {
                count()
            }

            live.on<GuildDeleteEvent> {
                error("Must not be executed")
            }

            // Wait that the 2 jobs are ready to listen the next events.
            // Without the delay, the success of the test is uncertain.
            delay(DELAY_TIME)

            val eventGuildBan = GuildBanAdd(
                DiscordGuildBan(
                    guildId = guildId.value,
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
                    id = guildId.value
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
