package dev.kord.rest.request

import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.DiscordMessage
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.MessageType.Default
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.readFile
import dev.kord.rest.service.ChannelService
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val mockId = Snowflake(42)
private const val fileName = "linus.png"
private val mockMessage = DiscordMessage(
    id = mockId,
    channelId = mockId,
    author = DiscordUser(id = mockId, username = "user", discriminator = "1337", avatar = null),
    content = "",
    timestamp = Clock.System.now(),
    editedTimestamp = null,
    tts = false,
    mentionEveryone = false,
    mentions = emptyList(),
    mentionRoles = emptyList(),
    attachments = listOf(
        DiscordAttachment(
            id = mockId,
            filename = fileName,
            size = 1234,
            url = "http://never.gonna.give.you.up",
            proxyUrl = "http://never.gonna.let.you.down",
        )
    ),
    embeds = emptyList(),
    pinned = false,
    type = Default,
)

class MessageRequests {
    @Test
    @JsName("test1")
    fun `attachment channel is read and closed lazily`() = runTest {

        val mockEngine = MockEngine { request ->
            request.body.toByteArray() // `toByteArray()` reads `fileChannel`

            respond(Json.encodeToString(mockMessage))
        }

        val channelService = ChannelService(KtorRequestHandler(client = HttpClient(mockEngine), token = ""))

        val fileChannel = readFile("images/kord.png")

        with(fileChannel) {
            assertFalse(isClosedForRead)
            assertTrue(totalBytesRead == 0L)

            val createdMessage = channelService.createMessage(mockId) {
                addFile(fileName, ChannelProvider { fileChannel })
            }
            assertEquals(createdMessage, mockMessage)

            assertTrue(isClosedForWrite)
            assertTrue(isClosedForRead)
            assertTrue(totalBytesRead > 0L)
        }
    }
}
