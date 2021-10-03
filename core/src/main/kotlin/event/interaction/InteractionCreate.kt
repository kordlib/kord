package dev.kord.core.event.interaction

import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.Event

public sealed interface InteractionCreateEvent : Event {
    public val interaction: Interaction
}
