package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
class MultiApplicationCommandBuilder {
    val commands = mutableListOf<ApplicationCommandCreateBuilder>()

    inline fun message(name: String, builder: MessageCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += MessageCommandCreateBuilder(name).apply(builder)
    }

    inline fun user(name: String, builder: UserCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += UserCommandCreateBuilder(name).apply(builder)
    }

    inline fun input(
        name: String,
        description: String,
        builder: ChatInputCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += ChatInputCreateBuilder(name, description).apply(builder)
    }

    fun build(): List<ApplicationCommandCreateRequest> {
        return commands.map { it.toRequest() }
    }
}
