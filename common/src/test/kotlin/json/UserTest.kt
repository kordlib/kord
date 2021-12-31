package json

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.UserFlags
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/user/$name.json").readText()
}

class UserTest {

    @Test
    fun `User serialization`() {
        val user = Json.decodeFromString(DiscordUser.serializer(), file("user"))

        with(user) {
            id.toString() shouldBe "80351110224678912"
            username shouldBe "Nelly"
            discriminator shouldBe "1337"
            avatar shouldBe "8342729096ea3675442027381ff50dfe"
            verified.asNullable!! shouldBe true
            email.value shouldBe "nelly@discordapp.com"
            flags.value shouldBe UserFlags(64)
            premiumType.value!!.value shouldBe 1
        }

    }
}
