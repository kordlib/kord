package dev.kord.core.event.interaction

import dev.kord.core.behavior.interaction.response.InteractionResponseBehavior
import dev.kord.core.entity.interaction.ActionInteraction
import dev.kord.core.entity.interaction.DataInteraction
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.Event

/**
 * An [Event] that fires when an [Interaction] is created.
 *
 * The event should be responded to within 3 seconds. See the methods and extensions of each [Interaction] type for
 * ways to respond to the particular interaction.
 *
 * Some response methods return an [InteractionResponseBehavior] that can be used for further operations on the
 * interaction.
 */
public sealed interface InteractionCreateEvent : Event {
    public val interaction: Interaction
}

/** An [Event] that fires when an [ActionInteraction] is created. */
public sealed interface ActionInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: ActionInteraction
}

/** An [Event] that fires when a [DataInteraction] is created. */
public sealed interface DataInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: DataInteraction
}
