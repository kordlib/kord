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
		val stream = ClassLoader.getSystemResourceAsStream("images/kord.png")!!

		MultipartRequest<Any, GatewayResponse>(Route.GatewayGet, mapOf(), StringValues.Empty, StringValues.Empty, null, listOf("linus.png" to stream))

		assertThrows<Exception> { stream.reset() }
	}
}
