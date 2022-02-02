package json

import dev.kord.gateway.Event
import dev.kord.gateway.Reconnect
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = SerializationTest::class.java.classLoader
    return loader.getResource("json/regression/$name.json").readText()
}

class RegressionTests {
    @Test
    fun `Resume command serialization`() {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("eventWithDataThatShouldNotHaveData"))
        event shouldBe Reconnect
    }

    @Test
    fun `Resumed with unknown data`() {
        Json.decodeFromString(Event.DeserializationStrategy, file("resumeWithUnknownData"))
    }

    @Test
    fun `PresenceReplace with unknown data`() {
        Json.decodeFromString(Event.DeserializationStrategy, file("presenceReplaceWithUnknownData"))
    }

    @Test
    fun `Unknown event with successfully parses`() {
        Json.decodeFromString(Event.DeserializationStrategy, file("eventWithUnknownData")) //dispatch event with non-existent type
    }

}
