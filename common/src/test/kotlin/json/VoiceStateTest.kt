@file:Suppress("EXPERIMENTAL_API_USAGE")

package json

import com.gitlab.kordlib.common.entity.DiscordVoiceState
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/voice/$name.json").readText()
}

class VoiceStateTest {

    @Test
    @Ignore("official documentation example is incorrect")
    fun `VoiceState serialization`() {
        val state = Json.decodeFromString(DiscordVoiceState.serializer(), file("voicestate"))

        with(state) {
            channelId!!.asString shouldBe "157733188964188161"
            userId.asString shouldBe "80351110224678912"
            sessionId shouldBe "90326bd25d71d39b9ef95b299e3872ff"
            deaf shouldBe false
            mute shouldBe false
            selfDeaf shouldBe false
            selfMute shouldBe true
            suppress shouldBe false
        }

    }
}
