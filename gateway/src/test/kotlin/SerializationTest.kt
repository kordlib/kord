import com.gitlab.hopebaron.websocket.Event
import com.gitlab.hopebaron.websocket.HelloEvent
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.json
import kotlinx.serialization.stringify
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

@ImplicitReflectionSerializer
object SerializationTest : Spek({
    Feature("Event (De)serialization.") {
        Scenario("Event is sent.") {
            val e = HelloEvent(1000, "kord")
            val content = Json.stringify(e)
            Then("Gets de-serialized") {
                val deserialized = Json.parse(Event.serializer(), content)
                assertEquals(json { "heartbeat_interval" to 1000
                    "_trace" to "kord"
                }, deserialized)
            }
        }
    }
})