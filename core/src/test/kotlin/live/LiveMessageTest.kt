package live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.live.*
import dev.kord.gateway.*
import equality.randomId
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
                        id = randomId(),
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
                count()
            }

            sendEventValidAndRandomId(messageId) {
                MessageReactionAdd(
                    MessageReactionAddData(
                        messageId = it,
                        channelId = randomId(),
                        userId = randomId(),
                        emoji = DiscordPartialEmoji(null, emojiExpected.name)
                    ),
                    0
                )
            }
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
                count()
            }

            fun createEvent(messageId: Snowflake, emoji: ReactionEmoji) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = messageId,
                    channelId = randomId(),
                    userId = randomId(),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            val eventRandomId = createEvent(randomId(), emojiExpected)
            sendEventAndWait(eventRandomId)

            val eventOtherReaction = createEvent(messageId, emojiOther)
            sendEventAndWait(eventOtherReaction)

            val event = createEvent(messageId, emojiExpected)
            sendEventAndWait(event)
        }
    }

    @Test
    fun `Check onReactionRemove is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemove {
                assertEquals(messageId, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                count()
            }

            sendEventValidAndRandomId(messageId) {
                MessageReactionRemove(
                    MessageReactionRemoveData(
                        messageId = it,
                        channelId = randomId(),
                        userId = randomId(),
                        emoji = DiscordPartialEmoji(null, emojiExpected.name)
                    ),
                    0
                )
            }
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
                count()
            }

            fun createEvent(messageId: Snowflake, emoji: ReactionEmoji) = MessageReactionRemove(
                MessageReactionRemoveData(
                    messageId = messageId,
                    channelId = randomId(),
                    userId = randomId(),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            val eventRandomId = createEvent(randomId(), emojiExpected)
            sendEventAndWait(eventRandomId)

            val eventOtherReaction = createEvent(messageId, emojiOther)
            sendEventAndWait(eventOtherReaction)

            val event = createEvent(messageId, emojiExpected)
            sendEventAndWait(event)
        }
    }

    @Test
    fun `Check onReactionRemoveAll is called when event is received`() {
        countdownContext(1) {
            live.onReactionRemoveAll {
                assertEquals(messageId, it.messageId)
                count()
            }

            sendEventValidAndRandomId(messageId) {
                MessageReactionRemoveAll(
                    AllRemovedMessageReactions(
                        channelId = randomId(),
                        messageId = it,
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(messageId, it.messageId)
                count()
            }

            sendEventValidAndRandomId(messageId) {
                MessageUpdate(
                    DiscordPartialMessage(
                        id = it,
                        channelId = randomId()
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onShutdown is called when event the message delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(messageId) {
                MessageDelete(
                    DeletedMessage(
                        id = it,
                        channelId = randomId()
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onShutdown is called when event the bulk delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(messageId) {
                MessageDeleteBulk(
                    BulkDeleteData(
                        ids = mutableListOf(it),
                        channelId = randomId()
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onShutdown is called when event the channel delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(channelId) {
                ChannelDelete(
                    DiscordChannel(
                        id = it,
                        type = ChannelType.GuildText
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onShutdown is called when event the guild delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(guildId) {
                GuildDelete(
                    DiscordUnavailableGuild(
                        id = it
                    ),
                    0
                )
            }
        }
    }
}