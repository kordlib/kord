package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.MessageReactionAddData
import dev.kord.common.entity.MessageType
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.live.*
import dev.kord.gateway.MessageReactionAdd
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class LiveMessageTest : AbstractLiveEntityTest<LiveMessage>() {

    @BeforeTest
    fun onBefore() = runBlocking {
        //message = createMessage(channel)
        live = LiveMessage(
            guildId = null,
            message = Message(
                kord = kord,
                data = MessageData(
                    id = Snowflake(0),
                    channelId = Snowflake(1),
                    author = UserData(
                        id = Snowflake(2),
                        username = "test",
                        discriminator = "test"
                    ),
                    content = "",
                    timestamp = "",
                    tts = false,
                    mentionEveryone = false,
                    mentions = emptyList(),
                    mentionRoles = emptyList(),
                    attachments = emptyList(),
                    embeds = emptyList(),
                    pinned = false,
                    type = MessageType.Default
                )
            )
        )
    }

    @Test
    fun `Check onReactionAdd is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionAdd {
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            val eventOtherMessage = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = Snowflake(1),
                    channelId = Snowflake(1),
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emojiExpected.name)
                ),
                0
            )
            sendEvent(eventOtherMessage)

            val event = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = Snowflake(0),
                    channelId = Snowflake(1),
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emojiExpected.name)
                ),
                0
            )
            sendEvent(event)
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

            live.onReactionAdd {
                println(it)
            }

            fun createEvent(emoji: ReactionEmoji) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = Snowflake(0),
                    channelId = Snowflake(1),
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            val eventOtherMessage = createEvent(emojiOther)
            sendEvent(eventOtherMessage)

            val event = createEvent(emojiExpected)
            sendEvent(event)
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