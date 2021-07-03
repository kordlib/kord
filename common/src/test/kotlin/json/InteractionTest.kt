package json

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
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
            arg.int() shouldBe 1
            arg.string() shouldBe "1"
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
            arg.int() shouldBe 1
            arg.string() shouldBe "1"
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
            assert(arg is CommandArgument<*>)
            arg as CommandArgument<*>
            arg.name shouldBe "testint"
            arg.int() shouldBe 1
            arg.string() shouldBe "1"
        }
    }

    @Test
    fun `slash command permissions can be serialized`() {
        val text = file("slash_command_permissions_update")

        val interaction = json.decodeFromString(DiscordGuildApplicationCommandPermissions.serializer(), text)

        with(interaction) {
            id shouldBe "833008574669651989"
            applicationId shouldBe "535129406650318860"
            guildId shouldBe "809471441719787602"

            with(permissions.first()) {
                id shouldBe "827126703301066792"
                type shouldBe DiscordGuildApplicationCommandPermission.Type.Role
                permission shouldBe true
            }
        }
    }

    @Test
    fun `select menu can be deserialized`() {
        val text = file("selectmenu")

        val interaction = json.decodeFromString(DiscordInteraction.serializer(), text)
        with(interaction) {
            applicationId shouldBe "845027738276462632"
            channelId shouldBe "772908445358620702"
            with(data){
                componentType shouldBe ComponentType.SelectMenu
                customId shouldBe "class_select_1"
                values shouldBe listOf("mage", "rogue")
            }
            guildId shouldBe "772904309264089089"
            id shouldBe "847587388497854464"
        }
    }

}
