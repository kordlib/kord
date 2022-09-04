package dev.kord.rest.request

import dev.kord.rest.NamedFile
import dev.kord.rest.json.response.GatewayResponse
import dev.kord.rest.route.Route
import io.ktor.util.*
import io.ktor.util.cio.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import kotlin.io.path.toPath

@TestMethodOrder(MethodOrderer.MethodName::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageRequests {
    @Test
    fun `attachment channel closed`() = runBlocking {
        val readChannel = ClassLoader.getSystemResource("images/kord.png").toURI().toPath().readChannel()

        assert(!readChannel.isClosedForWrite)

        MultipartRequest<Any, GatewayResponse>(
            Route.GatewayGet,
            mapOf(),
            StringValues.Empty,
            StringValues.Empty,
            null,
            listOf(NamedFile("linus.png", readChannel))
        )

        assert(readChannel.isClosedForRead)
    }
}
