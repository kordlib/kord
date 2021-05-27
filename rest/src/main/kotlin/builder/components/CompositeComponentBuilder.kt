package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.optional
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
class CompositeComponentBuilder {
    val components: MutableList<DiscordComponent> = mutableListOf()

    @OptIn(ExperimentalContracts::class)
    inline fun actionRow(builder: CompositeComponentBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRow(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun button(builder: ButtonBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ButtonBuilder().apply(builder).build())
    }

    fun toComponent(type: ComponentType) = DiscordComponent(type, components = components.optional())
}
