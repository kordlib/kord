package dev.kord.core.event.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event


sealed interface ApplicationCommandUpdateEvent : Event {
    val command: GuildApplicationCommand
    override val guildId: Snowflake?
        get() = command.guildId
}

class ChatInputCommandUpdateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandUpdateEvent


class UserCommandUpdateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandUpdateEvent


class MessageCommandUpdateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandUpdateEvent

class UnknownApplicationCommandUpdateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandUpdateEvent

