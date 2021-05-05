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
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
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
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(messageId: Snowflake) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = messageId,
                    channelId = channelId,
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emojiExpected.name)
                ),
                0
            )

            val eventOtherMessage = createEvent(createSuperiorId(messageId))
            sendEvent(eventOtherMessage)

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
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(emoji: ReactionEmoji) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = messageId,
                    channelId = channelId,
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

            fun createEvent(messageId: Snowflake) = MessageReactionRemove(
                MessageReactionRemoveData(
                    messageId = messageId,
                    channelId = channelId,
                    userId = Snowflake(2),
                    emoji = DiscordPartialEmoji(null, emojiExpected.name)
                ),
                0
            )

            val eventOtherMessage = createEvent(createSuperiorId(messageId))
            sendEvent(eventOtherMessage)

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
                assertEquals(emojiExpected, it.emoji)
                countDown()
            }

            fun createEvent(emoji: ReactionEmoji) = MessageReactionRemove(
                MessageReactionRemoveData(
                    messageId = messageId,
                    channelId = channelId,
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
    fun `Check onReactionRemoveAll is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemoveAll {
                countDown()
            }

            val event = MessageReactionRemoveAll(
                AllRemovedMessageReactions(
                    channelId = channelId,
                    messageId = messageId,
                ),
                0
            )
            sendEvent(event)
        }
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                countDown()
            }

            val event = MessageUpdate(
                DiscordPartialMessage(
                    id = messageId,
                    channelId = channelId
                ),
                0
            )

            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when event the message delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            val event = MessageDelete(
                DeletedMessage(
                    id = messageId,
                    channelId = channelId
                ),
                0
            )

            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when event the bulk delete event is received`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            val event = MessageDeleteBulk(
                BulkDeleteData(
                    ids = mutableListOf(messageId),
                    channelId = channelId
                ),
                0
            )
            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when event the channel delete event is received`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            val event = ChannelDelete(
                DiscordChannel(
                    id = channelId,
                    type = ChannelType.GuildText
                ),
                0
            )

            sendEvent(event)
        }
    }

    @Test
    fun `Check onShutdown is called when event the guild delete event is received`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            val event = GuildDelete(
                DiscordUnavailableGuild(
                    id = guildId
                ),
                0
            )

            sendEvent(event)
        }
    }

    fun createSuperiorId(messageId: Snowflake) = Snowflake(messageId.value + 1)
}