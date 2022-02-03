package dev.kord.core.event.interaction

import dev.kord.core.entity.interaction.ActionInteraction
import dev.kord.core.entity.interaction.DataInteraction
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.Event

public sealed interface InteractionCreateEvent<out I : Interaction> : Event {
    public val interaction: I
}

public sealed interface ActionInteractionCreateEvent<out I : ActionInteraction> : InteractionCreateEvent<I> {
    override val interaction: I
}

public sealed interface DataInteractionCreateEvent<out I : DataInteraction> : InteractionCreateEvent<I> {
    override val interaction: I
}
