package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext


public sealed interface ApplicationCommandDeleteEvent : Event {
    public val command: GuildApplicationCommand
}

public class ChatInputCommandDeleteEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandDeleteEvent, CoroutineScope by coroutineScope


public class UserCommandDeleteEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandDeleteEvent, CoroutineScope by coroutineScope


public class MessageCommandDeleteEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
    public  val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandDeleteEvent, CoroutineScope by coroutineScope


public class UnknownApplicationCommandDeleteEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ApplicationCommandDeleteEvent, CoroutineScope by coroutineScope
