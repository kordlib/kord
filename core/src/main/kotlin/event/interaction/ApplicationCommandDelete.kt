package dev.kord.core.event.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event


sealed interface ApplicationCommandDeleteEvent : Event {
    val command: GuildApplicationCommand
    override val guildId: Snowflake?
        get() = command.guildId
}

class ChatInputCommandDeleteEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandDeleteEvent


class UserCommandDeleteEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandDeleteEvent


class MessageCommandDeleteEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandDeleteEvent


class UnknownApplicationCommandDeleteEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandDeleteEvent
