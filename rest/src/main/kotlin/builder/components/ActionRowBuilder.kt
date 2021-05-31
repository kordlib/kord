package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.optional
import kotlin.contracts.ExperimentalContracts

@KordPreview
class ActionRowBuilder {
    val components = mutableListOf<DiscordComponent>()

    @OptIn(ExperimentalContracts::class)
    fun button(builder: ButtonBuilder.() -> Unit) {
        components.add(ButtonBuilder().apply(builder).build())
    }

    fun build(): DiscordComponent = DiscordComponent(ComponentType.ActionRow, components = components.optional())
}
