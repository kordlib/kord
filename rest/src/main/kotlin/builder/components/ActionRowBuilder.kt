package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
@Suppress("FunctionName")
@KordPreview
inline fun ActionRow(builder: CompositeComponentBuilder.() -> Unit): DiscordComponent {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return CompositeComponentBuilder().apply(builder).toComponent(ComponentType.ActionRow)
}
