package dev.kord.core

import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.serialization.serializer

suspend fun main(args: Array<String>) {
    println(serializer<AutoModerationRuleEventType>())
    val kord = Kord(args.firstOrNull() ?: error("token required"))

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.content == "!ping") message.channel.createMessage("pong")
    }

    kord.login {
        presence { playing("!ping to pong") }

        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
