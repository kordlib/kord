package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event

/**
 * The event dispatched when an Application command is created.
 *
 * See [Application command create](https://discord.com/developers/docs/interactions/application-commands#create-global-application-command)
 *
 * @property command The [GuildApplicationCommand] being created
 */
public sealed interface ApplicationCommandCreateEvent : Event {
    public val command: GuildApplicationCommand
}

/**
 * The event dispatched when a Chat Input command is created.
 *
 * @see ApplicationCommandCreateEvent
 */
public class ChatInputCommandCreateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent

/**
 * The event dispatched when a User command is created.
 *
 * @see ApplicationCommandCreateEvent
 */
public class UserCommandCreateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent

/**
 * The event dispatched when a Message command is created.
 *
 * @see ApplicationCommandCreateEvent
 */
public class MessageCommandCreateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent

/**
 * The event dispatched when an Unknown application command is created.
 *
 * @see ApplicationCommandCreateEvent
 */
public class UnknownApplicationCommandCreateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent
