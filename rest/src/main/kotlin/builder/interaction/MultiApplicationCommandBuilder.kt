package dev.kord.rest.builder.interaction

import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.DiscordApplicationCommand
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest

class MultiApplicationCommandBuilder {
    val commands = mutableListOf<ApplicationCommandCreateBuilder>()
    inline fun message(name: String, builder: MessageCommandCreateBuilder.() -> Unit) {
        commands += MessageCommandCreateBuilder(name).apply(builder)
    }

    inline fun user(name: String, builder: UserCommandCreateBuilder.() -> Unit) {
        commands += UserCommandCreateBuilder(name).apply(builder)
    }
    inline fun input(
        name: String,
        description: String,
        builder: ChatInputCreateBuilder.() -> Unit) {
        commands += ChatInputCreateBuilder(name, description).apply(builder)
    }

    fun build(): List<ApplicationCommandCreateRequest> {
        return commands.map { it.toRequest() }
    }
}