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
        else if (message.content.startsWith("!topic")) {
            val topic = message.content.substringAfter("topic").trim()
            val channelId = member?.getVoiceState()!!.channelId!!
            val channel = getGuild()!!.getChannelOf<StageChannel>(channelId)

            if (topic.isNotEmpty()) {
                channel.createStageInstance(topic)
            } else {
                message.channel.createMessage("Topic: ${channel.getStageInstance().topic}")
            }
        }
    }

    kord.login { playing("!ping to pong") }
}
