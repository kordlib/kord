@file:Suppress("EXPERIMENTAL_API_USAGE")

package json

import com.gitlab.kordlib.common.entity.DiscordUser
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/user/$name.json").readText()
}

class UserTest {

    @Test
    fun `User serialization`() {
        val user = Json.parse(DiscordUser.serializer(), file("user"))

        with(user) {
            id shouldBe "80351110224678912"
            username shouldBe "Nelly"
            discriminator shouldBe "1337"
            avatar shouldBe "8342729096ea3675442027381ff50dfe"
            verified shouldBe true
            email shouldBe "nelly@discordapp.com"
            flags shouldBe 64
            premiumType!!.code shouldBe 1
        }

    }
}

