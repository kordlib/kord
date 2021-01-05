package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.Event

class InteractionCreateEvent(
    val interaction: Interaction,
    override val kord: Kord,
    override val shard: Int
) : Event