package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import dev.kord.core.entity.interaction.GlobalAutoCompleteInteraction
import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import dev.kord.core.event.Event

/** The [Event] dispatched when an [AutoCompleteInteraction] is created. */
public sealed interface AutoCompleteInteractionCreateEvent : DataInteractionCreateEvent {
    override val interaction: AutoCompleteInteraction
}

/** The [Event] dispatched when a [GlobalAutoCompleteInteraction] is created. */
public class GlobalAutoCompleteInteractionCreateEvent(
    override val kord: Kord,
    override val shard: Int,
    override val interaction: GlobalAutoCompleteInteraction,
    override val customContext: Any?,
) : AutoCompleteInteractionCreateEvent

/** The [Event] dispatched when a [GuildAutoCompleteInteraction] is created. */
public class GuildAutoCompleteInteractionCreateEvent(
    override val kord: Kord,
    override val shard: Int,
    override val interaction: GuildAutoCompleteInteraction,
    override val customContext: Any?,
) : AutoCompleteInteractionCreateEvent
