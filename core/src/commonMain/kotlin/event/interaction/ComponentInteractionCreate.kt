package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.Event

/** An [Event] that fires when a [ComponentInteraction] is created. */
public sealed interface ComponentInteractionCreateEvent : ActionInteractionCreateEvent {
    override val interaction: ComponentInteraction
}

/** An [Event] that fires when a [GlobalComponentInteraction] is created. */
public sealed interface GlobalComponentInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: GlobalComponentInteraction
}

/** An [Event] that fires when a [GuildComponentInteraction] is created. */
public sealed interface GuildComponentInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: GuildComponentInteraction
}

/** An [Event] that fires when a [ButtonInteraction] is created. */
public sealed interface ButtonInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: ButtonInteraction
}

/** An [Event] that fires when a [SelectMenuInteraction] is created. */
public sealed interface SelectMenuInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: SelectMenuInteraction
}

/** An [Event] that fires when a [GuildButtonInteraction] is created. */
public class GuildButtonInteractionCreateEvent(
    override val interaction: GuildButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ButtonInteractionCreateEvent, GuildComponentInteractionCreateEvent

/** An [Event] that fires when a [GlobalButtonInteraction] is created. */
public class GlobalButtonInteractionCreateEvent(
    override val interaction: GlobalButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ButtonInteractionCreateEvent, GlobalComponentInteractionCreateEvent

/** An [Event] that fires when a [GuildSelectMenuInteraction] is created. */
public class GuildSelectMenuInteractionCreateEvent(
    override val interaction: GuildSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : SelectMenuInteractionCreateEvent, GuildComponentInteractionCreateEvent

/** An [Event] that fires when a [GlobalSelectMenuInteraction] is created. */
public class GlobalSelectMenuInteractionCreateEvent(
    override val interaction: GlobalSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : SelectMenuInteractionCreateEvent, GlobalComponentInteractionCreateEvent
