package json

import com.gitlab.hopebaron.websocket.HeartbeatACK
import com.gitlab.hopebaron.websocket.HelloEvent
import com.gitlab.hopebaron.websocket.ReceivePayload
import com.gitlab.hopebaron.websocket.Reconnect
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/event/$name.json").readText()
}

class SerializationTest : Spek({

    describe("HeartbeatACK") {
        it("deserializes a HeartbeatACK object") {
            val payload = Json.parse(ReceivePayload.serializer(), file("ack"))
            val event = payload.event
            assertEquals(event, HeartbeatACK)
        }
    }

    describe("Hello") {
        it("deserializes a Hello object") {
            val payload = Json.parse(ReceivePayload.serializer(), file("hello"))
            val event = payload.event as HelloEvent
            assertEquals(event.heartbeatInterval, 1337)
            assertEquals(event.traces, listOf("test"))
        }
    }

    describe("Reconnect") {
        it("deserializes a Reconnect object") {
            val payload = Json.parse(ReceivePayload.serializer(), file("reconnect"))
            val event = payload.event
            assertEquals(event, Reconnect)
        }
    }
})