package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.live.*
import dev.kord.gateway.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
class LiveMessageTest : AbstractLiveEntityTest<LiveMessage>() {

    private lateinit var messageId: Snowflake

    private lateinit var channelId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        messageId = Snowflake(0)
        channelId = Snowflake(1)
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveMessage(
            guildId = guildId,
            message = Message(
                kord = kord,
                data = MessageData(
                    id = messageId,
                    channelId = channelId,
                    author = UserData(
                        id = Snowflake(2),
                        username = "",
                        discriminator = ""
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
                assertEquals(channelId, it.channelId)
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(channelId: Snowflake, messageId: Snowflake) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = messageId,
                    channelId = channelId,
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emojiExpected.name)
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), messageId)
            sendEvent(eventRandomChannel)

            val eventRandomMessage = createEvent(channelId, randomId())
            sendEvent(eventRandomMessage)

            val event = createEvent(channelId, messageId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onReactionAdd with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionAdd(emojiExpected) {
                assertEquals(channelId, it.channelId)
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(channelId: Snowflake, messageId: Snowflake, emoji: ReactionEmoji) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = messageId,
                    channelId = channelId,
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), messageId, emojiExpected)
            sendEvent(eventRandomChannel)

            val eventRandomMessage = createEvent(channelId, randomId(), emojiExpected)
            sendEvent(eventRandomMessage)

            val eventOtherReaction = createEvent(channelId, messageId, emojiOther)
            sendEvent(eventOtherReaction)

            val event = createEvent(channelId, messageId, emojiExpected)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onReactionRemove is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemove {
                assertEquals(channelId, it.channelId)
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(channelId: Snowflake, messageId: Snowflake) = MessageReactionRemove(
                MessageReactionRemoveData(
                    messageId = messageId,
                    channelId = channelId,
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emojiExpected.name)
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), messageId)
            sendEvent(eventRandomChannel)

            val eventRandomMessage = createEvent(channelId, randomId())
            sendEvent(eventRandomMessage)

            val event = createEvent(channelId, messageId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onReactionRemove with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionRemove(emojiExpected) {
                assertEquals(channelId, it.channelId)
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(channelId: Snowflake, messageId: Snowflake, emoji: ReactionEmoji) = MessageReactionRemove(
                MessageReactionRemoveData(
                    messageId = messageId,
                    channelId = channelId,
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), messageId, emojiExpected)
            sendEvent(eventRandomChannel)

            val eventRandomMessage = createEvent(channelId, randomId(), emojiExpected)
            sendEvent(eventRandomMessage)

            val eventOtherReaction = createEvent(channelId, messageId, emojiOther)
            sendEvent(eventOtherReaction)

            val event = createEvent(channelId, messageId, emojiExpected)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onReactionRemoveAll is called when event is received`() {
        countdownContext(1) {
            live.onReactionRemoveAll {
                assertEquals(channelId, it.channelId)
                assertEquals(messageId, it.messageId)
                countDown()
            }

            fun createEvent(channelId: Snowflake, messageId: Snowflake) = MessageReactionRemoveAll(
                AllRemovedMessageReactions(
                    channelId = channelId,
                    messageId = messageId,
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), messageId)
            sendEvent(eventRandomChannel)

            val eventRandomMessage = createEvent(channelId, randomId())
            sendEvent(eventRandomMessage)

            val event = createEvent(channelId, messageId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(channelId, it.channelId)
                assertEquals(messageId, it.messageId)
                countDown()
            }

            fun createEvent(channelId: Snowflake, messageId: Snowflake) = MessageUpdate(
                DiscordPartialMessage(
                    id = messageId,
                    channelId = channelId
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), messageId)
            sendEvent(eventRandomChannel)

            val eventRandomMessage = createEvent(channelId, randomId())
            sendEvent(eventRandomMessage)

            val event = createEvent(channelId, messageId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when event the message delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(channelId: Snowflake, messageId: Snowflake) = MessageDelete(
                DeletedMessage(
                    id = messageId,
                    channelId = channelId
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), messageId)
            sendEvent(eventRandomChannel)

            assertTrue { live.isActive }

            val eventRandomMessage = createEvent(channelId, randomId())
            sendEvent(eventRandomMessage)

            assertTrue { live.isActive }

            val event = createEvent(channelId, messageId)
            sendEvent(event)

            assertFalse { live.isActive }
        }
    }

    @Test
    fun `Check onShutdown is called when event the bulk delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(channelId: Snowflake, messageId: Snowflake) = MessageDeleteBulk(
                BulkDeleteData(
                    ids = mutableListOf(messageId),
                    channelId = channelId
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId(), messageId)
            sendEvent(eventRandomChannel)

            assertTrue { live.isActive }

            val eventRandomMessage = createEvent(channelId, randomId())
            sendEvent(eventRandomMessage)

            assertTrue { live.isActive }

            val event = createEvent(channelId, messageId)
            sendEvent(event)

            assertFalse { live.isActive }
        }
    }

    @Test
    fun `Check onShutdown is called when event the channel delete event is received`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(channelId: Snowflake) = ChannelDelete(
                DiscordChannel(
                    id = channelId,
                    type = ChannelType.GuildText
                ),
                0
            )

            val eventRandomChannel = createEvent(randomId())
            sendEvent(eventRandomChannel)

            assertTrue { live.isActive }

            val event = createEvent(channelId)
            sendEvent(event)

            assertFalse { live.isActive }
        }
    }

    @Test
    fun `Check onShutdown is called when event the guild delete event is received`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(guildId: Snowflake) = GuildDelete(
                DiscordUnavailableGuild(
                    id = guildId
                ),
                0
            )

            val eventRandomGuild = createEvent(randomId())
            sendEvent(eventRandomGuild)

            assertTrue { live.isActive }

            val event = createEvent(channelId)
            sendEvent(event)

            assertFalse { live.isActive }
        }
    }
}