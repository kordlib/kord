package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.*


sealed interface ComponentInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: ComponentInteraction
}

sealed interface GlobalComponentInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: GlobalComponentInteraction
}


sealed interface GuildComponentInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: GuildComponentInteraction
}


sealed interface ButtonInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: ButtonInteraction
}


sealed interface  SelectMenuInteractionCreateEvent : ComponentInteractionCreateEvent {
    override val interaction: SelectMenuInteraction
}


class GuildButtonInteractionCreateEvent(
    override val interaction: GuildButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
    ) : ButtonInteractionCreateEvent, GuildComponentInteractionCreateEvent


class GlobalButtonInteractionCreateEvent(
    override val interaction: GlobalButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
    ) : ButtonInteractionCreateEvent, GlobalComponentInteractionCreateEvent



class GuildSelectMenuInteractionCreateEvent(
    override val interaction: GuildSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
    ) : SelectMenuInteractionCreateEvent, GuildComponentInteractionCreateEvent


class GlobalSelectMenuInteractionCreateEvent(
    override val interaction: GlobalSelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
    ) : SelectMenuInteractionCreateEvent, GlobalComponentInteractionCreateEvent
