package dev.kord.core.event.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.entity.interaction.SelectMenuInteraction


sealed interface ComponentInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: ComponentInteraction
    override val guildId: Snowflake?
        get() = interaction.data.guildId.value
}


class ButtonInteractionCreateEvent(
    override val interaction: ButtonInteraction,
    override val kord: Kord,
    override val shard: Int,
) : ComponentInteractionCreateEvent


class SelectMenuInteractionCreateEvent(
    override val interaction: SelectMenuInteraction,
    override val kord: Kord,
    override val shard: Int,
) : ComponentInteractionCreateEvent