package dev.kord.core.event.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.*
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.Event

/**
 * This event fires when an interaction is created.
 *
 *
 * Discord currently have one type of interactions,
 * [Slash Commands][dev.kord.core.entity.interaction.ApplicationCommand].
 *
 * The event should be acknowledged using one of the following methods:
 * * [acknowledgeEphemeral][Interaction.acknowledgeEphemeral] - acknowledges an interaction ephemerally.
 * * [acknowledgePublic][Interaction.acknowledgePublic] - acknowledges an interaction in public.
 * * [respondPublic][Interaction.respondPublic] - same as public acknowledgement, but an immediate result (message) can be supplied.
 * * [respondEphemeral][Interaction.respondEphemeral] - same as ephemeral acknowledgement, but an immediate result (message) can be supplied.
 *
 * Once an interaction has been acknowledged,
 * you can use the [PublicInteractionResponseBehavior.followUp] or [EphemeralInteractionResponseBehavior.followUp].
 *
 * The resulting followup message and its methods may defer based on which method is used.
 */
@KordPreview
class InteractionCreateEvent(
    val interaction: Interaction,
    override val kord: Kord,
    override val shard: Int
) : Event