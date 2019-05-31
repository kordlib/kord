package json

import com.gitlab.hopebaron.websocket.entity.Snowflake
import com.gitlab.hopebaron.websocket.entity.VoiceState
import kotlinx.serialization.json.Json
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/voice/$name.json").readText()
}

class VoiceStateTest : Spek({

    describe("voicestate") {
        it("is deserialized correctly") {
            val state = Json.parse(VoiceState.serializer(), file("voicestate"))

            with(state) {
                channelId shouldBe Snowflake("157733188964188161")
                userId shouldBe Snowflake("80351110224678912")
                sessionId shouldBe Snowflake("90326bd25d71d39b9ef95b299e3872ff")
                deaf shouldBe false
                mute shouldBe false
                selfDeaf shouldBe false
                selfMute shouldBe true
                suppress shouldBe false
            }

        }
    }

})