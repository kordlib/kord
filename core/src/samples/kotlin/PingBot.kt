import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createStageInstance
import dev.kord.core.behavior.channel.getStageInstance
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.channel.StageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on

suspend fun main(args: Array<String>) {
    val kord = Kord(args.firstOrNull() ?: error("token required"))

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.content == "!ping") message.channel.createMessage("pong")
    }

    kord.login { playing("!ping to pong") }
}
