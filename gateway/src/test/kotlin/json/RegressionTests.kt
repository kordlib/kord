package json

import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.HeartbeatACK
import com.gitlab.kordlib.gateway.Reconnect
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = SerializationTest::class.java.classLoader
    return loader.getResource("json/regression/$name.json").readText()
}

class RegressionTests {
    @Test
    fun `Resume command serialization`() {
        val event = Json.parse(Event.Companion, file("eventWithDataThatShouldNotHaveData"))
        event shouldBe Reconnect
    }

}