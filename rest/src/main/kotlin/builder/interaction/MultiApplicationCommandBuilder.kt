package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.DiscordApplicationCommand
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
@KordDsl
class MultiApplicationCommandBuilder {
    val commands = mutableListOf<ApplicationCommandCreateBuilder>()

    @OptIn(ExperimentalContracts::class)
    inline fun message(name: String, builder: MessageCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += MessageCommandCreateBuilder(name).apply(builder)
    }

    @OptIn(ExperimentalContracts::class)
    inline fun user(name: String, builder: UserCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += UserCommandCreateBuilder(name).apply(builder)
    }
    @OptIn(ExperimentalContracts::class)
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