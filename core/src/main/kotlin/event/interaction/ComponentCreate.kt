package dev.kord.core.event.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.entity.component.ButtonComponent
import dev.kord.core.entity.component.Component
import dev.kord.core.entity.component.SelectMenuComponent
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.core.event.Event


sealed interface ComponentCreateEvent : InteractionCreateEvent {
    override val interaction: ComponentInteraction
}


class ButtonCreateEvent(
    override val interaction: ButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
) : ComponentCreateEvent


class SelectMenuCreateEvent(
    override val interaction: SelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
) : ComponentCreateEvent