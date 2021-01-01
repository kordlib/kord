package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.core.event.Event

class ApplicationCommandUpdateEvent(
    val command: GuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int
) : Event

