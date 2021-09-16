package dev.kord.core.event.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event


sealed interface ApplicationCommandCreateEvent : Event {
    val command: GuildApplicationCommand
    override val guildId: Snowflake?
        get() = command.guildId
}

class ChatInputCommandCreateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandCreateEvent


class UserCommandCreateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandCreateEvent


class MessageCommandCreateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandCreateEvent


class UnknownApplicationCommandCreateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
) : ApplicationCommandCreateEvent