package dev.kord.rest.request

import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.ChannelService
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import kotlin.io.path.toPath

@TestMethodOrder(MethodOrderer.MethodName::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageRequests {
    @Test
    fun `attachment channel closed`() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{
                    "id": "1234", 
                    "type": 0,
                    "content": "FOO", 
                    "channel_id": "1234", 
                    "author": {
                        "id": "1234", 
                        "username": "Bar", 
                        "avatar": null, 
                        "discriminator": "1337"
                    }, 
                    "attachments": [ 
                        {
                            "id": "1234", 
                            "filename": "linus.png",
                            "size": 1234,
                            "url": "http://never.gonna.give.you.up",
                            "proxy_url": "http://never.gonna.let.you.down"
                        } 
                    ], 
                    "embeds": [], 
                    "mentions": [], 
                    "mention_roles": [], 
                    "pinned": false, 
                    "mention_everyone": false, 
                    "tts": false, 
                    "timestamp": "1987-07-27T12:00:00.000000+00:00", 
                    "edited_timestamp": null 
                }""".trimIndent().encodeToByteArray())
            )
        }

        val client = HttpClient(mockEngine)
        val handler = KtorRequestHandler(client = client, token = "")
        val service = ChannelService(handler)

        val readChannel = ClassLoader.getSystemResource("images/kord.png").toURI().toPath().readChannel()
        readChannel.awaitContent()

        assert(!readChannel.isClosedForWrite)

        service.createMessage(Snowflake(0)) {
            addFile("linus.png", readChannel)
        }

        assert(readChannel.isClosedForRead)
    }
}
