package live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.live.*
import dev.kord.gateway.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
class LiveMessageTest : AbstractLiveEntityTest<LiveMessage>() {

    private lateinit var messageId: Snowflake

    private lateinit var channelId: Snowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        messageId = randomId()
        channelId = randomId()
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
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(messageId: Snowflake) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = messageId,
                    channelId = randomId(),
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emojiExpected.name)
                ),
                0
            )

            val eventRandomId = createEvent(randomId())
            sendEvent(eventRandomId)

            val event = createEvent(messageId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onReactionAdd with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionAdd(emojiExpected) {
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(messageId: Snowflake, emoji: ReactionEmoji) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = messageId,
                    channelId = randomId(),
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            val eventRandomId = createEvent(randomId(), emojiExpected)
            sendEvent(eventRandomId)

            val eventOtherReaction = createEvent(messageId, emojiOther)
            sendEvent(eventOtherReaction)

            val event = createEvent(messageId, emojiExpected)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onReactionRemove is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemove {
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(messageId: Snowflake) = MessageReactionRemove(
                MessageReactionRemoveData(
                    messageId = messageId,
                    channelId = randomId(),
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emojiExpected.name)
                ),
                0
            )


            val eventRandomId = createEvent(randomId())
            sendEvent(eventRandomId)

            val event = createEvent(messageId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onReactionRemove with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionRemove(emojiExpected) {
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(messageId: Snowflake, emoji: ReactionEmoji) = MessageReactionRemove(
                MessageReactionRemoveData(
                    messageId = messageId,
                    channelId = randomId(),
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            val eventRandomId = createEvent(randomId(), emojiExpected)
            sendEvent(eventRandomId)

            val eventOtherReaction = createEvent(messageId, emojiOther)
            sendEvent(eventOtherReaction)

            val event = createEvent(messageId, emojiExpected)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onReactionRemoveAll is called when event is received`() {
        countdownContext(1) {
            live.onReactionRemoveAll {
                assertEquals(messageId, it.messageId)
                countDown()
            }

            fun createEvent(messageId: Snowflake) = MessageReactionRemoveAll(
                AllRemovedMessageReactions(
                    channelId = randomId(),
                    messageId = messageId,
                ),
                0
            )

            val eventRandomId = createEvent(randomId())
            sendEvent(eventRandomId)

            val event = createEvent(messageId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(messageId, it.messageId)
                countDown()
            }

            fun createEvent(messageId: Snowflake) = MessageUpdate(
                DiscordPartialMessage(
                    id = messageId,
                    channelId = randomId()
                ),
                0
            )

            val eventRandomId = createEvent(randomId())
            sendEvent(eventRandomId)

            val event = createEvent(messageId)
            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when event the message delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(messageId: Snowflake) = MessageDelete(
                DeletedMessage(
                    id = messageId,
                    channelId = randomId()
                ),
                0
            )

            val eventRandomId = createEvent(randomId())
            sendEvent(eventRandomId)
            waitAndCheckLiveIsActive()

            val event = createEvent(messageId)
            sendEvent(event)
            waitAndCheckLiveIsInactive()
        }
    }

    @Test
    fun `Check onShutdown is called when event the bulk delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            fun createEvent(messageId: Snowflake) = MessageDeleteBulk(
                BulkDeleteData(
                    ids = mutableListOf(messageId),
                    channelId = randomId()
                ),
                0
            )

            val eventRandomId = createEvent(randomId())
            sendEvent(eventRandomId)
            waitAndCheckLiveIsActive()

            val event = createEvent(messageId)
            sendEvent(event)
            waitAndCheckLiveIsInactive()
        }
    }

    @Test
    fun `Check onShutdown is called when event the channel delete event is received`() {
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
            waitAndCheckLiveIsActive()

            val event = createEvent(channelId)
            sendEvent(event)
            waitAndCheckLiveIsInactive()
        }
    }

    @Test
    fun `Check onShutdown is called when event the guild delete event is received`() {
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
            waitAndCheckLiveIsActive()

            val event = createEvent(guildId)
            sendEvent(event)
            waitAndCheckLiveIsInactive()
        }
    }
}