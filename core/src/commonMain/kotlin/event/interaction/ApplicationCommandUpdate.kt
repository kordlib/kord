package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event

/**
 * The event dispatched when an [GuildApplicationCommand] is updated.
 *
 * See [application command update](https://discord.com/developers/docs/interactions/application-commands#edit-guild-application-command)
 *
 * @property command The command that was updated
 */
public sealed interface ApplicationCommandUpdateEvent : Event {
    public val command: GuildApplicationCommand
}

/**
 * The event dispatched when a [GuildChatInputCommand] is updated
 *
 * @see ApplicationCommandUpdateEvent
 */
public class ChatInputCommandUpdateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandUpdateEvent

/**
 * The event dispatched when a [GuildUserCommand] is updated
 *
 * @see ApplicationCommandUpdateEvent
 */
public class UserCommandUpdateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandUpdateEvent

/**
 * The event dispatched when a [GuildMessageCommand] is updated
 *
 * @see ApplicationCommandUpdateEvent
 */
public class MessageCommandUpdateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandUpdateEvent

/**
 * The event dispatched when an [UnknownGuildApplicationCommand] is updated
 *
 * @see ApplicationCommandUpdateEvent
 */
public class UnknownApplicationCommandUpdateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandUpdateEvent
