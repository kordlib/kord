package live

import BoxedSnowflake
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
import equality.randomId
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
class LiveMessageTest : AbstractLiveEntityTest<LiveMessage>() {

    private lateinit var messageId: BoxedSnowflake

    private lateinit var channelId: BoxedSnowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        messageId = BoxedSnowflake(randomId())
        channelId = BoxedSnowflake(randomId())
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveMessage(
            guildId = guildId.value,
            message = Message(
                kord = kord,
                data = MessageData(
                    id = messageId.value,
                    channelId = channelId.value,
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
                assertEquals(messageId.value, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                count()
            }

            sendEventValidAndRandomId(messageId.value) {
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
                assertEquals(messageId.value, it.messageId)
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
            sendEventAndWait(createEvent(messageId.value, emojiOther))
            sendEvent(createEvent(messageId.value, emojiExpected))
        }
    }

    @Test
    fun `Check onReactionRemove is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemove {
                assertEquals(messageId.value, it.messageId)
                assertEquals(emojiExpected, it.emoji)
                count()
            }

            sendEventValidAndRandomId(messageId.value) {
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
                assertEquals(messageId.value, it.messageId)
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
            sendEvent(createEvent(messageId.value, emojiOther))
            sendEvent(createEvent(messageId.value, emojiExpected))
        }
    }

    @Test
    fun `Check onReactionRemoveAll is called when event is received`() {
        countdownContext(1) {
            live.onReactionRemoveAll {
                assertEquals(messageId.value, it.messageId)
                count()
            }

            sendEventValidAndRandomId(messageId.value) {
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
                assertEquals(messageId.value, it.messageId)
                count()
            }

            sendEventValidAndRandomId(messageId.value) {
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
    fun `Check if live entity is completed when event the message delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as MessageDeleteEvent
                assertEquals(messageId.value, event.messageId)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(messageId.value) {
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
    fun `Check if live entity is completed when event the bulk delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as MessageBulkDeleteEvent
                assertTrue { messageId.value in event.messageIds }
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(messageId.value) {
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
    fun `Check if live entity is completed when event the channel delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as ChannelDeleteEvent
                assertEquals(channelId.value, event.channel.id)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(channelId.value) {
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
    fun `Check if live entity is completed when event the guild delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as GuildDeleteEvent
                assertEquals(guildId.value, event.guildId)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(guildId.value) {
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
