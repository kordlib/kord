package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.createTextChannel
import dev.kord.core.behavior.createCategory
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.live.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class LiveMessageTest {

    private val token = System.getenv("KORD_TEST_TOKEN")

    private lateinit var kord: Kord

    private lateinit var guild: Guild

    private lateinit var category: Category

    private lateinit var channel: TextChannel

    private lateinit var message: Message

    private lateinit var live: LiveMessage

    @BeforeAll
    fun onBeforeAll() = runBlocking {
        kord = Kord(token)
        GlobalScope.launch(kord.coroutineContext) {
            kord.login()
        }

        guild = createGuild()
        category = createCategory()
        channel = createChannel()
    }

    @AfterAll
    fun onAfterAll() = runBlocking {
        try {
            guild.delete()
        } finally {
            kord.logout()
            kord.shutdown()
        }
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        message = createMessage()
        live = message.live()
    }

    @AfterTest
    fun onAfter() {
        live.shutDown()
    }

    private suspend fun createGuild(): Guild = kord.createGuild("LIVE_MESSAGE_TEST_GUILD") {}

    private suspend fun createCategory(): Category = guild.createCategory("LIVE_MESSAGE_TEST_CATEGORY")

    private suspend fun createChannel(): TextChannel = category.createTextChannel("LIVE_MESSAGE_TEST_CHANNEL")

    private suspend fun createMessage(): Message = channel.createMessage("LIVE_MESSAGE_TEST_MESSAGE")

    @Test
    fun `Shutdown method cancel the lifecycle`() = runBlockingTest {
        assertTrue(live.isActive)
        live.shutDown()
        assertFalse(live.isActive)
    }

    @Test
    fun `Children job are cancel when the live entity is shutdown`() = runBlocking {
        val job = live.onReactionAdd { }
        assertTrue(job.isActive)

        message.addReaction(ReactionEmoji.Unicode("\uD83D\uDC28"))

        live.shutDown()
        assertTrue(job.isCancelled)
        assertFalse(live.isActive)
    }

    @Test
    fun `Check onReactionAdd is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionAdd {
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            message.addReaction(emojiExpected)
        }
    }

    @Test
    fun `Check onReactionAdd with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionAdd(emojiExpected) {
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            message.addReaction(emojiOther)
            message.addReaction(emojiExpected)
        }
    }

    @Test
    fun `Check onReactionRemove is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemove {
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            message.addReaction(emojiExpected)
            message.deleteOwnReaction(emojiExpected)
        }
    }

    @Test
    fun `Check onReactionRemove with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionRemove(emojiExpected) {
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            message.addReaction(emojiExpected)
            message.addReaction(emojiOther)
            message.deleteOwnReaction(emojiOther)
            message.deleteOwnReaction(emojiExpected)
        }
    }

    @Test
    fun `Check onReactionRemoveAll is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemoveAll {
                countDown()
            }

            message.addReaction(emojiExpected)
            message.deleteAllReactions()
        }
    }

    @Ignore
    @Test
    fun `Check onCreate is called when event is received`() {
        countdownContext(1) {
            live.onCreate {
                countDown()
            }

            // Create message already created ?
        }
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {

            live.onUpdate {
                countDown()
            }

            message.edit {
                content = message.content
            }
        }
    }

    @Ignore
    @Test
    fun `Check onOnlyDelete is called when event is received`() {
        countdownContext(1) {

            live.onOnlyDelete {
                countDown()
            }

            message.delete()
            delay(500)
            assertFalse(live.isActive)
        }
    }

    @Ignore
    @Test
    fun `Check onBulkDelete is called when event is received`() {
        countdownContext(1) {

            live.onBulkDelete {
                countDown()
            }

            channel.bulkDelete(listOf(message.id))
            delay(500)
            assertFalse(live.isActive)
        }
    }

    @Ignore
    @Test
    fun `Check onChannelDelete is not called because the liveEntity is shutdown`() {
        countdownContext(1) {

            live.onChannelDelete {
                countDown()
            }

            channel.delete()
            delay(500)
            assertFalse(live.isActive)

            channel = createChannel()
        }
    }

    @Ignore
    @Test
    fun `Check onGuildDelete is not called because the liveEntity is shutdown`() {
        countdownContext(1) {

            live.onGuildDelete {
                countDown()
            }

            guild.delete()
            delay(500)
            assertFalse(live.isActive)

            guild = createGuild()
            category = createCategory()
            channel = createChannel()
        }
    }

    private inline fun countdownContext(
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
}