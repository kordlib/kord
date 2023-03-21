package json

import dev.kord.common.entity.DiscordVoiceState
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/voice/$name.json")!!.readText()
}

class VoiceStateTest {

    @Test
    fun `VoiceState serialization`() {
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
