package dev.kord.core

import dev.kord.core.behavior.channel.PollParentChannelBehavior
import dev.kord.core.behavior.channel.createPoll
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlin.time.Duration.Companion.minutes

suspend fun main(args: Array<String>) {
    val kord = Kord(args.firstOrNull() ?: error("token required"))

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.content == "!ping") message.channel.createMessage("pong")
        if (message.content == "!poll") {
            (message.channel as PollParentChannelBehavior).createPoll {
                    question("Does this work?")

                    answer("Yes")
                    answer("No")

                    expiresIn(5.minutes)
                }
            }
        }

    kord.login {
        presence { playing("!ping to pong") }

        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
