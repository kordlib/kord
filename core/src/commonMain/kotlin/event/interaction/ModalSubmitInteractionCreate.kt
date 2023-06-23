package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.GlobalModalSubmitInteraction
import dev.kord.core.entity.interaction.GuildModalSubmitInteraction
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.core.event.Event

/** An [Event] that fires when a [ModalSubmitInteraction] is created. */
public sealed interface ModalSubmitInteractionCreateEvent : ActionInteractionCreateEvent {
    override val interaction: ModalSubmitInteraction
}

/** An [Event] that fires when a [GuildModalSubmitInteraction] is created. */
public class GuildModalSubmitInteractionCreateEvent(
    override val interaction: GuildModalSubmitInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ModalSubmitInteractionCreateEvent

/** An [Event] that fires when a [GlobalModalSubmitInteraction] is created. */
public class GlobalModalSubmitInteractionCreateEvent(
    override val interaction: GlobalModalSubmitInteraction,
    override val shard: Int,
    override val kord: Kord,
    override val customContext: Any?,
) : ModalSubmitInteractionCreateEvent
