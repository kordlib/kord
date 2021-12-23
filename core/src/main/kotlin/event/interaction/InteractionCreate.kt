package dev.kord.core.event.interaction

import dev.kord.core.entity.interaction.ActionInteraction
import dev.kord.core.entity.interaction.DataInteraction
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.Event

public sealed interface InteractionCreateEvent : Event {
    public val interaction: Interaction
}

public sealed interface ActionInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: ActionInteraction
}

public sealed interface DataInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: DataInteraction
}
