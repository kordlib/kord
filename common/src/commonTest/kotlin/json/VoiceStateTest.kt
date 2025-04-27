package dev.kord.common.json

import dev.kord.common.entity.DiscordVoiceState
import dev.kord.common.readFile
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test

private suspend fun file(name: String): String = readFile("voice", name)

@IgnoreOnSimulatorPlatforms
class VoiceStateTest {

    @Test
    @JsName("test1")
    fun `VoiceState serialization`() = runTest {
        val state = Json.decodeFromString(DiscordVoiceState.serializer(), file("voicestate"))

        with(state) {
            channelId shouldBe "157733188964188161"
            userId shouldBe "80351110224678912"
            sessionId shouldBe "90326bd25d71d39b9ef95b299e3872ff"
            deaf shouldBe false
            mute shouldBe false
            selfDeaf shouldBe false
            selfVideo shouldBe true
            selfMute shouldBe true
            suppress shouldBe false
            requestToSpeakTimestamp shouldBe Instant.parse("2021-03-31T18:45:31.297561+00:00")
        }

    }
}
