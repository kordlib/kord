package interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ApplicationCommandOptionType
import dev.kord.common.entity.InteractionCallbackData
import dev.kord.core.cache.data.ApplicationInteractionData
import dev.kord.core.entity.interaction.*
import kotlinx.serialization.json.*
import mockKord
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class CommandsTypeTests {
    val arg = buildJsonObject {
        put("type", ApplicationCommandOptionType.Integer.type)
        put("name", "argument")
        put("value", 1)
    }
    val root = buildJsonObject {
        put("id", "792107855418490901")
        put("name", "root")
        putJsonArray("options") { add(arg) }
    }
    val partialSubCommand = buildJsonObject {
        put("type", ApplicationCommandOptionType.SubCommand.type)
        put("name", "subCommand")
        putJsonArray("options") { add(arg) }
    }
    val subCommand =
        buildJsonObject {
            putJsonArray("options") { add(partialSubCommand) }
            put("name", "root")
            put("id", "792107855418490901")
        }

    val group = buildJsonObject {

        putJsonArray("options") {
            addJsonObject {
                put("type", ApplicationCommandOptionType.SubCommandGroup.type)
                put("name", "group")
                putJsonArray("options") { add(partialSubCommand) }
            }
        }
        put("name", "root")
        put("id", "792107855418490901")
    }

    @Test
    fun `Correctly infer RootCommand`() {
        val serializedRoot = Json.decodeFromJsonElement(InteractionCallbackData.serializer(), root)
        val data = ApplicationInteractionData.from(serializedRoot, null)
        val command = InteractionCommand(data, mockKord())
        assert(command is RootCommand)
        command as RootCommand
        assertEquals(1L, command.integers["argument"])
        assertEquals("root", command.rootName)

    }

    @Test
    fun `Correctly infer subcommand`() {
        val sub = Json.decodeFromJsonElement(InteractionCallbackData.serializer(), subCommand)
        val data = ApplicationInteractionData.from(sub, null)
        val command = InteractionCommand(data, mockKord())
        assert(command is SubCommand)
        command as SubCommand
        assertEquals(1L, command.integers["argument"])
        assertEquals("root", command.rootName)
        assertEquals("subCommand", command.name)

    }

    @Test
    fun `Correctly infer group`() {
        val grouping = Json.decodeFromJsonElement(InteractionCallbackData.serializer(), group)
        val data = ApplicationInteractionData.from(grouping, null)
        val command = InteractionCommand(data, mockKord())
        assert(command is GroupCommand)
        command as GroupCommand
        assertEquals(1L, command.integers["argument"])
        assertEquals("root", command.rootName)
        assertEquals("group", command.groupName)
        assertEquals("subCommand", command.name)
    }
}