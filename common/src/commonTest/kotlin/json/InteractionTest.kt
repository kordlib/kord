package dev.kord.common.json

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.readFile
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertIs

private suspend fun file(name: String): String = readFile("interaction", name)

class InteractionTest {

    val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    @JsName("test1")
    fun `group command can be deserialized`() = runTest {
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
            assertIs<CommandGroup>(group)
            group.name shouldBe "group"
            val subCommand = group.options.orEmpty().first()
            subCommand.name shouldBe "groupsubcommand"
            val arg = subCommand.options.orEmpty().first()
            arg.type shouldBe ApplicationCommandOptionType.Integer
            arg.name shouldBe "testint"
            arg.value shouldBe 1L
            appPermissions shouldBe Permissions("2147483647")
        }
    }

    @Test
    @JsName("test2")
    fun `subcommand  can be deserialized`() = runTest {
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
            assertIs<SubCommand>(subCommand)
            subCommand.name shouldBe "subcommand"
            val arg = subCommand.options.orEmpty().first()
            arg.type shouldBe ApplicationCommandOptionType.Integer
            arg.name shouldBe "testint"
            arg.value shouldBe 1L
            appPermissions shouldBe Permissions("2147483647")
        }
    }


    @Test
    @JsName("test3")
    fun `root  can be deserialized`() = runTest {
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
            assertIs<CommandArgument<*>>(arg)
            arg.type shouldBe ApplicationCommandOptionType.Integer
            arg.name shouldBe "testint"
            arg.value shouldBe 1L
            appPermissions shouldBe Permissions("2147483647")
        }
    }

    @Test
    @JsName("test4")
    fun `slash command permissions can be serialized`() = runTest {
        val text = file("slash_command_permissions_update")

        val interaction = json.decodeFromString(DiscordGuildApplicationCommandPermissions.serializer(), text)

        with(interaction) {
            id shouldBe "833008574669651989"
            applicationId shouldBe "535129406650318860"
            guildId shouldBe "809471441719787602"

            with(permissions.first()) {
                id shouldBe "827126703301066792"
                type shouldBe ApplicationCommandPermissionType.Role
                permission shouldBe true
            }
        }
    }

    @Test
    @JsName("test5")
    fun `select menu can be deserialized`() = runTest {
        val text = file("selectmenu")

        val interaction = json.decodeFromString(DiscordInteraction.serializer(), text)
        with(interaction) {
            applicationId shouldBe "845027738276462632"
            channelId shouldBe "772908445358620702"
            with(data) {
                componentType shouldBe ComponentType.StringSelect
                customId shouldBe "class_select_1"
                values shouldBe listOf("mage", "rogue")
            }
            guildId shouldBe "772904309264089089"
            id shouldBe "847587388497854464"
            appPermissions shouldBe Permissions("2147483647")
        }
    }

}
