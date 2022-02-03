package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope


public sealed interface ComponentInteractionCreateEvent<out I : ComponentInteraction> : InteractionCreateEvent<I> {
    override val interaction: I
}

public sealed interface GlobalComponentInteractionCreateEvent<out I : GlobalComponentInteraction> :
    ComponentInteractionCreateEvent<I> {
    override val interaction: I
}


public sealed interface GuildComponentInteractionCreateEvent<out I : GuildComponentInteraction> :
    ComponentInteractionCreateEvent<I> {
    override val interaction: I
}


public sealed interface ButtonInteractionCreateEvent<out I : ButtonInteraction> : ComponentInteractionCreateEvent<I> {
    override val interaction: I
}


public sealed interface SelectMenuInteractionCreateEvent<out I : SelectMenuInteraction> :
    ComponentInteractionCreateEvent<I> {
    override val interaction: I
}


public class GuildButtonInteractionCreateEvent(
    override val interaction: GuildButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ButtonInteractionCreateEvent<GuildButtonInteraction>,
    GuildComponentInteractionCreateEvent<GuildButtonInteraction>,
    CoroutineScope by coroutineScope


public class GlobalButtonInteractionCreateEvent(
    override val interaction: GlobalButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ButtonInteractionCreateEvent<GlobalButtonInteraction>,
    GlobalComponentInteractionCreateEvent<GlobalButtonInteraction>,
    CoroutineScope by coroutineScope


public class GuildSelectMenuInteractionCreateEvent(
    override val interaction: GuildSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : SelectMenuInteractionCreateEvent<GuildSelectMenuInteraction>,
    GuildComponentInteractionCreateEvent<GuildSelectMenuInteraction>,
    CoroutineScope by coroutineScope


public class GlobalSelectMenuInteractionCreateEvent(
    override val interaction: GlobalSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : SelectMenuInteractionCreateEvent<GlobalSelectMenuInteraction>,
    GlobalComponentInteractionCreateEvent<GlobalSelectMenuInteraction>,
    CoroutineScope by coroutineScope
