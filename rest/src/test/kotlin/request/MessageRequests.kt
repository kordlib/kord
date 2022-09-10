package dev.kord.rest.request

import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.DiscordMessage
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.MessageType.Default
import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.ChannelService
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.io.path.toPath

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
    fun `attachment channel is read and closed lazily`() = runBlocking {

        val mockEngine = MockEngine { request ->
            request.body.toByteArray() // `toByteArray()` reads `fileChannel`

            respond(Json.encodeToString(mockMessage))
        }

        val channelService = ChannelService(KtorRequestHandler(client = HttpClient(mockEngine), token = ""))

        val fileChannel = ClassLoader.getSystemResource("images/kord.png")!!.toURI().toPath().readChannel()

        with(fileChannel) {
            assert(!isClosedForWrite)
            assert(!isClosedForRead)
            assert(totalBytesRead == 0L)

            val createdMessage = channelService.createMessage(mockId) {
                addFile(fileName, ChannelProvider { fileChannel })
            }
            assert(createdMessage == mockMessage)

            assert(isClosedForWrite)
            assert(isClosedForRead)
            assert(totalBytesRead > 0L)
        }
    }
}
