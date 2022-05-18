package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public sealed class MultiApplicationCommandBuilder {
    public val commands: MutableList<ApplicationCommandCreateBuilder> = mutableListOf()

    public fun build(): List<ApplicationCommandCreateRequest> {
        return commands.map { it.toRequest() }
    }
}
public inline fun MultiApplicationCommandBuilder.input(
    name: String,
    description: String,
    builder: ChatInputCreateBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    commands += ChatInputCreateBuilderImpl(name, description).apply(builder)
}

public inline fun MultiApplicationCommandBuilder.message(
    name: String,
    builder: MessageCommandCreateBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    commands += MessageCommandCreateBuilderImpl(name).apply(builder)
}


public inline fun MultiApplicationCommandBuilder.user(name: String, builder: UserCommandCreateBuilder.() -> Unit = {}) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    commands += UserCommandCreateBuilderImpl(name).apply(builder)
}

@KordDsl
public class GlobalMultiApplicationCommandBuilder : MultiApplicationCommandBuilder() {
    public inline fun input(name: String, description: String, builder: GlobalChatInputCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += ChatInputCreateBuilderImpl(name, description).apply(builder)
    }

    public inline fun message(name: String, builder: GlobalMessageCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += MessageCommandCreateBuilderImpl(name).apply(builder)
    }


    public inline fun user(name: String, builder: GlobalUserCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += UserCommandCreateBuilderImpl(name).apply(builder)
    }
}

@KordDsl
public class GuildMultiApplicationCommandBuilder : MultiApplicationCommandBuilder() {
    public inline fun input(name: String, description: String, builder: ChatInputCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += ChatInputCreateBuilderImpl(name, description).apply(builder)
    }

    public inline fun message(name: String, builder: MessageCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += MessageCommandCreateBuilderImpl(name).apply(builder)
    }


    public inline fun user(name: String, builder: UserCommandCreateBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        commands += UserCommandCreateBuilderImpl(name).apply(builder)
    }
}
