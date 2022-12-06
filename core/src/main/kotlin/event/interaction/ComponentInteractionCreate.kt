package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.Event

/** The [Event] dispatched when a [ComponentInteraction] is created. */
public sealed interface ComponentInteractionCreateEvent : ActionInteractionCreateEvent {
    override val interaction: ComponentInteraction
}

/** The [Event] dispatched when a [GlobalComponentInteraction] is created. */
public sealed interface GlobalComponentInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: GlobalComponentInteraction
}

/** The [Event] dispatched when a [GuildComponentInteraction] is created. */
public sealed interface GuildComponentInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: GuildComponentInteraction
}

/** The [Event] dispatched when a [ButtonInteraction] is created. */
public sealed interface ButtonInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: ButtonInteraction
}

/** The [Event] dispatched when a [SelectMenuInteraction] is created. */
public sealed interface SelectMenuInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: SelectMenuInteraction
}

/** The [Event] dispatched when a [GuildButtonInteraction] is created. */
public class GuildButtonInteractionCreateEvent(
    override val interaction: GuildButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ButtonInteractionCreateEvent, GuildComponentInteractionCreateEvent

/** The [Event] dispatched when a [GlobalButtonInteraction] is created. */
public class GlobalButtonInteractionCreateEvent(
    override val interaction: GlobalButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ButtonInteractionCreateEvent, GlobalComponentInteractionCreateEvent

/** The [Event] dispatched when a [GuildSelectMenuInteraction] is created. */
public class GuildSelectMenuInteractionCreateEvent(
    override val interaction: GuildSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : SelectMenuInteractionCreateEvent, GuildComponentInteractionCreateEvent

/** The [Event] dispatched when a [GlobalSelectMenuInteraction] is created. */
public class GlobalSelectMenuInteractionCreateEvent(
    override val interaction: GlobalSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : SelectMenuInteractionCreateEvent, GlobalComponentInteractionCreateEvent
