package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.*
import kotlin.coroutines.CoroutineContext


public sealed interface ComponentInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: ComponentInteraction
}

public sealed interface GlobalComponentInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: GlobalComponentInteraction
}


public sealed interface GuildComponentInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: GuildComponentInteraction
}


public sealed interface ButtonInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: ButtonInteraction
}


public sealed interface SelectMenuInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: SelectMenuInteraction
}


public class GuildButtonInteractionCreateEvent(
    override val interaction: GuildButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ButtonInteractionCreateEvent, GuildComponentInteractionCreateEvent


public class GlobalButtonInteractionCreateEvent(
    override val interaction: GlobalButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ButtonInteractionCreateEvent, GlobalComponentInteractionCreateEvent


public class GuildSelectMenuInteractionCreateEvent(
    override val interaction: GuildSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : SelectMenuInteractionCreateEvent, GuildComponentInteractionCreateEvent


public class GlobalSelectMenuInteractionCreateEvent(
    override val interaction: GlobalSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : SelectMenuInteractionCreateEvent, GlobalComponentInteractionCreateEvent
