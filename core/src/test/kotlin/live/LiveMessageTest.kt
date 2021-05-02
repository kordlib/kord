package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createTextChannel
import dev.kord.core.behavior.createCategory
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.Event
import dev.kord.core.live.LiveMessage
import dev.kord.core.live.live
import dev.kord.core.live.on
import dev.kord.core.live.onReactionAdd
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.*
import kotlin.test.Test
import kotlin.time.seconds

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
    fun setup() = runBlocking {
        kord = Kord(token)
        guild = createGuild()
        category = createCategory()
        channel = createChannel()
    }

    @AfterAll
    fun onAfterAll() = runBlocking {
        guild.delete()
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        message = createMessage()
        live = message.live()
    }

    @AfterTest
    fun onAfter() {
        if(live.isActive){
            live.shutDown()
        }
    }

    private suspend fun createGuild(): Guild {
        val region = kord.regions.first()

        return kord.createGuild("LIVE_MESSAGE_TEST_GUILD") {
            this.region = region.id
        }
    }

    private suspend fun createCategory(): Category {
        return guild.createCategory("LIVE_MESSAGE_TEST_CATEGORY")
    }

    private suspend fun createChannel(): TextChannel {
        return category.createTextChannel("LIVE_MESSAGE_TEST_CHANNEL")
    }

    private suspend fun createMessage(): Message {
        return channel.createMessage("test")
    }

    @Test
    fun `Shutdown method cancel the lifecycle`() = runBlockingTest {
        assertTrue(live.isActive)
        live.shutDown()
        assertFalse(live.isActive)
    }

    @Test
    fun `Children job are cancel when the live entity is shutdown`() = runBlockingTest {
        val job = live.onReactionAdd { }
        assertTrue(job.isActive)

        live.shutDown()
        assertTrue(job.isCancelled)
    }

    @Test
    fun `Check onReactionAdd is called when ReactionAddEvent is received`() = runBlocking {
        val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

        val counter = AtomicInteger(0)
        val countdown = CountDownLatch(1)

        live.onReactionAdd {
            println(it)
            assertEquals(emojiExpected, it.emoji)
            counter.incrementAndGet()
            countdown.countDown()
        }

        message.addReaction(emojiExpected)


        while(countdown.count != 0L && counter.get() == 0){
            delay(1000)
            countdown.countDown()
        }

        assertEquals(1, counter.get())
    }
}