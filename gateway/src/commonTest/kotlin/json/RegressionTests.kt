package dev.kord.gateway.json

import dev.kord.gateway.Event
import dev.kord.gateway.Reconnect
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test

private suspend fun file(name: String): String = readFile("regression", name)

@IgnoreOnSimulatorPlatforms
class RegressionTests {
    @Test
    @JsName("test1")
    fun `Resume command serialization`() = runTest {
        val event = Json.decodeFromString(Event.DeserializationStrategy, file("eventWithDataThatShouldNotHaveData"))
        event shouldBe Reconnect
    }

    @Test
    @JsName("test2")
    fun `Resumed with unknown data`() = runTest {
        Json.decodeFromString(Event.DeserializationStrategy, file("resumeWithUnknownData"))
    }

    @Test
    @JsName("test3")
    fun `PresenceReplace with unknown data`() = runTest {
        Json.decodeFromString(Event.DeserializationStrategy, file("presenceReplaceWithUnknownData"))
    }

    @Test
    @JsName("test4")
    fun `Unknown event with successfully parses`() = runTest {
        Json.decodeFromString(Event.DeserializationStrategy, file("eventWithUnknownData")) //dispatch event with non-existent type
    }

}
