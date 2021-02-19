package interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordApplicationCommandInteractionData
import dev.kord.common.entity.int
import dev.kord.common.entity.optional.optional
import dev.kord.core.cache.data.ApplicationCommandInteractionData
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.entity.interaction.RootCommand
import dev.kord.core.entity.interaction.SubCommand
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Test
import kotlin.reflect.typeOf
import kotlin.test.assertEquals
@KordPreview
class CommandsTypeTests {
    val arg = buildJsonObject {
        put("name", "argument")
        put("value", 1)
    }
    val root = buildJsonObject {
        put("id", "792107855418490901")
        put("name", "root")
        putJsonArray("options") { add(arg) }
    }
    val partialSubCommand = buildJsonObject {
        put("name",  "subCommand")
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
                put("name", "group")
                putJsonArray("options") { add(partialSubCommand) }
            }
        }
        put("name", "root")
        put("id", "792107855418490901")
    }

    @Test
    fun `Correctly infer RootCommand`() {
        val serializedRoot = Json.decodeFromJsonElement(DiscordApplicationCommandInteractionData.serializer(), root)
        val data = ApplicationCommandInteractionData.from(serializedRoot)
        val command = InteractionCommand(data)
        assert(command is RootCommand)
        command as RootCommand
        assertEquals(1, command.options["argument"]?.int())
        assertEquals("root", command.rootName)

    }

    @Test
    fun `Correctly infer subcommand`() {
        val sub = Json.decodeFromJsonElement(DiscordApplicationCommandInteractionData.serializer(), subCommand)
        val data = ApplicationCommandInteractionData.from(sub)
        val command = InteractionCommand(data)
        assert(command is SubCommand)
        command as SubCommand
        assertEquals(1, command.options["argument"]?.int())
        assertEquals("root", command.rootName)
        assertEquals("subCommand", command.name)

    }

    @Test
    fun `Correctly infer group`() {
        val grouping = Json.decodeFromJsonElement(DiscordApplicationCommandInteractionData.serializer(), group)
        val data = ApplicationCommandInteractionData.from(grouping)
        val command = InteractionCommand(data)
        assert(command is GroupCommand)
        command as GroupCommand
        assertEquals(1, command.options["argument"]?.int())
        assertEquals("root", command.rootName)
        assertEquals("group", command.groupName)
        assertEquals("subCommand", command.name)
    }
}