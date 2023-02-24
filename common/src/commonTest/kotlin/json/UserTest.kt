package json

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.UserFlags
import dev.kord.common.readFile
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test

private suspend fun file(name: String): String = readFile("user", name)

class UserTest {

    @Test
    @JsName("test1")
    fun `User serialization`() = runTest{
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
