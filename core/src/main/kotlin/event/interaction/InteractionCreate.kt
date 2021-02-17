package dev.kord.core.event.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.Event

@KordPreview
class InteractionCreateEvent(
        val interaction: Interaction,
        override val kord: Kord,
        override val shard: Int
) : Event