package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public class MultiApplicationCommandBuilder {
    public val commands: MutableList<ApplicationCommandCreateBuilder> = mutableListOf()

    public inline fun message(name: String, builder: MessageCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += MessageCommandCreateBuilder(name).apply(builder)
    }

    public inline fun user(name: String, builder: UserCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += UserCommandCreateBuilder(name).apply(builder)
    }

    public inline fun input(name: String, description: String, builder: ChatInputCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += ChatInputCreateBuilder(name, description).apply(builder)
    }

    public fun build(): List<ApplicationCommandCreateRequest> {
        return commands.map { it.toRequest() }
    }
}
