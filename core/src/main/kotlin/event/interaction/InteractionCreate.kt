package dev.kord.core.event.interaction

import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.Event

sealed interface InteractionCreateEvent : Event {
    val interaction: Interaction
}