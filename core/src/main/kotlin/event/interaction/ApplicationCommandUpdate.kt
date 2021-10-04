package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event
import kotlin.coroutines.CoroutineContext


public sealed interface ApplicationCommandUpdateEvent : Event {
    public val command: GuildApplicationCommand
}

public class ChatInputCommandUpdateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ApplicationCommandUpdateEvent


public class UserCommandUpdateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ApplicationCommandUpdateEvent


public class MessageCommandUpdateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ApplicationCommandUpdateEvent

public class UnknownApplicationCommandUpdateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : ApplicationCommandUpdateEvent
