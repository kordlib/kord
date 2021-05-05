package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.live.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.util.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class LiveMessageTest : AbstractLiveEntityTest<LiveMessage>() {

    private lateinit var category: Category

    private lateinit var channel: TextChannel

    private lateinit var message: Message

    @BeforeAll
    override fun onBeforeAll() = runBlocking {
        super.onBeforeAll()
        guild = createGuild()
        category = createCategory(guild!!)
        channel = createTextChannel(category)
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        message = createMessage()
        live = message.live()
    }

    private suspend fun createMessage(): Message = channel.createMessage(UUID.randomUUID().toString())

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

            // Message already created ?
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

    @Test
    fun `Check onShutdown is called when event the message delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }
            message.delete()
        }
    }

    @Test
    fun `Check onShutdown is called when event the bulk delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }
            channel.bulkDelete(listOf(message.id))
        }
    }

    @Test
    fun `Check onShutdown is called when event the channel delete event is received`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }
            channel.delete()
        }
        channel = createTextChannel(category)
    }

    @Test
    fun `Check onShutdown is called when event the guild delete event is received`() = runBlocking {
        val oldGuild = requireGuild()
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }
            oldGuild.delete()
        }

        assertFalse(live.isActive)
        guild = createGuild()
        category = createCategory(guild!!)
        channel = createTextChannel(category)
    }
}