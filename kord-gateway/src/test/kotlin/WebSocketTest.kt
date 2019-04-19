import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.json
import kotlinx.serialization.stringify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object WebSocketTest:Spek({
    Feature("Receiving Events") {
        Scenario("Gateway sends a Event"){
            Then("Event is parsed into an Event object. and passed.") {
             val event = Event(1,json{})
                val string = Json.stringify(Event.serializer(),event)
                println(string)
            }
        }
    }


})



@ImplicitReflectionSerializer
val webSocketSessionMockk = mockk<WebSocketSession> {
    val list = mutableListOf<Event>()
    Event(10,null)
    coEvery { incoming.iterator().next() } returns Frame.Text(Json.stringify(list.iterator().next()))
}
