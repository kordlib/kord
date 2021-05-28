package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.optional
import kotlin.contracts.ExperimentalContracts

@KordPreview
class CompositeComponentBuilder : CompositeActionRowBuilder, CompositeButtonBuilder {
    val components: MutableList<DiscordComponent> = mutableListOf()

    override fun actionRow(builder: CompositeButtonBuilder.() -> Unit) {
        components.add(ActionRow(builder))
    }

    @OptIn(ExperimentalContracts::class)
    override fun button(builder: ButtonBuilder.() -> Unit) {
        components.add(ButtonBuilder().apply(builder).build())
    }

    fun toComponent(type: ComponentType) = DiscordComponent(type, components = components.optional())
}
