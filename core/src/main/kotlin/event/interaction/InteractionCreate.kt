package dev.kord.core.event.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.entity.interaction.Interaction
@KordPreview
class InteractionCreateEvent(
    val interaction: Interaction,
    override val kord: Kord,
    override val shard: Int
) : Event