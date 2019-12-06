@file:Suppress("EXPERIMENTAL_API_USAGE")

package json

import com.gitlab.kordlib.common.entity.DiscordVoiceState
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/voice/$name.json").readText()
}

class VoiceStateTest {

    @Test
    fun `VoiceState serialization`() {
        val state = Json.parse(DiscordVoiceState.serializer(), file("voicestate"))

        with(state) {
            channelId shouldBe "157733188964188161"
            userId shouldBe "80351110224678912"
            sessionId shouldBe "90326bd25d71d39b9ef95b299e3872ff"
            deaf shouldBe false
            mute shouldBe false
            selfDeaf shouldBe false
            selfMute shouldBe true
            suppress shouldBe false
        }

    }
}
