package live

import dev.kord.common.entity.*
import dev.kord.core.cache.data.MessageData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.channel.ChannelDeleteEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.message.MessageBulkDeleteEvent
import dev.kord.core.event.message.MessageDeleteEvent
import dev.kord.core.live.*
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.gateway.*
import randomId
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.js.JsName
import kotlin.test.*

@Ignore
class LiveMessageTest : AbstractLiveEntityTest<LiveMessage>() {

    private val messageId: Snowflake = randomId()

    private val channelId: Snowflake = randomId()


    @BeforeTest
    fun onBefore() = runTest {
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
                    timestamp = Instant.fromEpochMilliseconds(0),
                    tts = false,
                    mentionEveryone = false,
                    mentions = emptyList(),
                    mentionRoles = emptyList(),
                    attachments = emptyList(),
                    embeds = emptyList(),
                    pinned = false,
                    type = MessageType.Default,
                )
            )
        )
    }

    @Test
    @JsName("test1")
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
    @JsName("test2")
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

            sendEventAndWait(createEvent(randomId(), emojiExpected))
            sendEventAndWait(createEvent(messageId, emojiOther))
            sendEvent(createEvent(messageId, emojiExpected))
        }
    }

    @Test
    @JsName("test3")
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
    @JsName("test4")
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

            sendEvent(createEvent(randomId(), emojiExpected))
            sendEvent(createEvent(messageId, emojiOther))
            sendEvent(createEvent(messageId, emojiExpected))
        }
    }

    @Test
    @JsName("test5")
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
    @JsName("test6")
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
    @JsName("test7")
    fun `Check if live entity is completed when event the message delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as MessageDeleteEvent
                assertEquals(messageId, event.messageId)
                runTest {
                    count()
                }
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
    @JsName("test8")
    fun `Check if live entity is completed when event the bulk delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as MessageBulkDeleteEvent
                assertTrue { messageId in event.messageIds }
                runTest {
                    count()
                }
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
    @JsName("test9")
    fun `Check if live entity is completed when event the channel delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as ChannelDeleteEvent
                assertEquals(channelId, event.channel.id)
                runTest {
                    count()
                }
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
    @JsName("test10")
    fun `Check if live entity is completed when event the guild delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as GuildDeleteEvent
                assertEquals(guildId, event.guildId)
                runTest {
                    count()
                }
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
