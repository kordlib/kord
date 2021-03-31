package json

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.filterList
import dev.kord.common.entity.optional.orEmpty
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.coroutines.suspendCoroutine
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private fun file(name: String): String {
    val loader = InteractionTest::class.java.classLoader
    return loader.getResource("json/interaction/$name.json")!!.readText()
}

@OptIn(KordPreview::class)
class InteractionTest {

    val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `group command can be deserialized`() {
        val text = file("groupsubcommand")

        val interaction = json.decodeFromString(DiscordInteraction.serializer(), text)
        with(interaction) {
            channelId shouldBe "587324906702766226"
            applicationId shouldBe "297153970613387264"
            id shouldBe "793442788670832640"
            version shouldBe 1
            type.type shouldBe 2
            token shouldBe "hunter2"
            data.name shouldBe "testsubcommands"
            data.id shouldBe "792107855418490901"
            val group = data.options.orEmpty().first()
            assert(group is CommandGroup)
            group as CommandGroup
            group.name shouldBe "group"
            val subCommand = group.options.orEmpty().first()
            subCommand.name shouldBe "groupsubcommand"
            val arg = subCommand.options.orEmpty().first()
            arg.name shouldBe "testint"
            arg.value.int() shouldBe 1
            arg.value.string() shouldBe "1"
        }
    }

    @Test
    fun `subcommand  can be deserialized`() {
        val text = file("subcommand")

        val interaction = json.decodeFromString(DiscordInteraction.serializer(), text)
        with(interaction) {
            channelId shouldBe "587324906702766226"
            applicationId shouldBe "297153970613387264"
            id shouldBe "793442788670832640"
            version shouldBe 1
            type.type shouldBe 2
            token shouldBe "hunter2"
            data.name shouldBe "testsubcommands"
            data.id shouldBe "792107855418490901"
            val subCommand = data.options.orEmpty().first()
            assert(subCommand is SubCommand)
            subCommand as SubCommand
            subCommand.name shouldBe "subcommand"
            val arg = subCommand.options.orEmpty().first()
            arg.name shouldBe "testint"
            arg.value.int() shouldBe 1
            arg.value.string() shouldBe "1"
        }
    }


    @Test
    fun `root  can be deserialized`() {
        val text = file("rootcommand")

        val interaction = json.decodeFromString(DiscordInteraction.serializer(), text)
        with(interaction) {
            channelId shouldBe "587324906702766226"
            applicationId shouldBe "297153970613387264"
            id shouldBe "793442788670832640"
            version shouldBe 1
            type.type shouldBe 2
            token shouldBe "hunter2"
            data.name shouldBe "testsubcommands"
            data.id shouldBe "792107855418490901"
            val arg = data.options.orEmpty().first()
            assert(arg is CommandArgument)
            arg as CommandArgument
            arg.name shouldBe "testint"
            arg.value.int() shouldBe 1
            arg.value.string() shouldBe "1"
        }
    }
}