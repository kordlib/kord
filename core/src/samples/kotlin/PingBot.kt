import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.respond
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on

@OptIn(KordPreview::class)
suspend fun main(args: Array<String>) {
    val kord = Kord(args.firstOrNull() ?: error("token required"))

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.content == "!ping") message.channel.createMessage("pong")
    }

    val command = kord.slashCommands.createGuildApplicationCommand(
        Snowflake(809471441719787602),
        "name", "desc"
    ) {
        defaultPermission = false
    }

    command.editPermissions(Snowflake(809471441719787602)) {
        role(Snowflake(820963217261854731))
    }

    kord.on<InteractionCreateEvent> {
        if (interaction.command.rootName == "name") {
            interaction.respond {
                content = "HI"
            }
        }
    }

    kord.login { playing("!ping to pong") }

}
