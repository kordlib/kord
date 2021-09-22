package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.entity.interaction.SelectMenuInteraction
import kotlin.coroutines.CoroutineContext


sealed interface ComponentInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: ComponentInteraction
}


class ButtonInteractionCreateEvent(
    override val interaction: ButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ComponentInteractionCreateEvent


class SelectMenuInteractionCreateEvent(
    override val interaction: SelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ComponentInteractionCreateEvent