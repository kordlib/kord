import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.respond
import dev.kord.core.event.gateway.ReadyEvent
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

    kord.on<ReadyEvent> {
        kord.slashCommands.createGlobalApplicationCommand(
            "ping", "Allows you to play ping pong"
        )
    }

    kord.on<InteractionCreateEvent> {
        when (interaction.command.name) {
            "ping" -> {
                interaction.respond(true) {
                    content = "Pong!"
                }
            }
        }
    }

    kord.login { playing("!ping to pong") }
}
