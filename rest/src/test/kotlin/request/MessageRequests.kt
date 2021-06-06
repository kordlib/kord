package dev.kord.rest.request

import dev.kord.rest.json.response.GatewayResponse
import dev.kord.rest.route.Route
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import java.io.InputStream

@TestMethodOrder(MethodOrderer.MethodName::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageRequests {
	@Test
	fun `attachment stream closed`() = runBlocking {
		val linusStream = object : InputStream() {
			val stream = ClassLoader.getSystemResourceAsStream("images/kord.png")!!
			var closed = false; private set
			override fun read() = stream.read()
			override fun close() { stream.close(); closed = true }
		}

		MultipartRequest<Any, GatewayResponse>(Route.GatewayGet, mapOf(), StringValues.Empty, StringValues.Empty, null, listOf("linus.png" to linusStream))

		assert(linusStream.closed)
	}
}
