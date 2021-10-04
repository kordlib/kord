package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event
import kotlin.coroutines.CoroutineContext


public sealed interface ApplicationCommandCreateEvent : Event {
    public val command: GuildApplicationCommand
}

public class ChatInputCommandCreateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ApplicationCommandCreateEvent


public class UserCommandCreateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ApplicationCommandCreateEvent


public class MessageCommandCreateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ApplicationCommandCreateEvent


public class UnknownApplicationCommandCreateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ApplicationCommandCreateEvent
