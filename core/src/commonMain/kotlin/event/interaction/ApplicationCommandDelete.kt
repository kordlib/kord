package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event

/**
 * The event dispatched when an Application command is deleted.
 *
 * See [Application command create](https://discord.com/developers/docs/interactions/application-commands#create-global-application-command)
 *
 * @property command The [GuildApplicationCommand] being deleted
 */

@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. The deprecation level will be raised to HIDDEN in 0.16.0 and this declaration will be removed in " +
        "0.17.0.",
    level = DeprecationLevel.ERROR,
)
public sealed interface ApplicationCommandDeleteEvent : Event {
    public val command: GuildApplicationCommand
}

/**
 * The event dispatched when a chat input command is deleted.
 *
 * @see ApplicationCommandDeleteEvent
 */
@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. The deprecation level will be raised to HIDDEN in 0.16.0 and this declaration will be removed in " +
        "0.17.0.",
    level = DeprecationLevel.ERROR,
)
public class ChatInputCommandDeleteEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandDeleteEvent

/**
 * The event dispatched when a user command is deleted.
 *
 * @see ApplicationCommandDeleteEvent
 */

@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. The deprecation level will be raised to HIDDEN in 0.16.0 and this declaration will be removed in " +
        "0.17.0.",
    level = DeprecationLevel.ERROR,
)
public class UserCommandDeleteEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandDeleteEvent

/**
 * The event dispatched when a message command is deleted.
 *
 * @see ApplicationCommandDeleteEvent
 */

@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. The deprecation level will be raised to HIDDEN in 0.16.0 and this declaration will be removed in " +
        "0.17.0.",
    level = DeprecationLevel.ERROR,
)
public class MessageCommandDeleteEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandDeleteEvent

/**
 * The event dispatched when an unknown application command is deleted.
 *
 * @see ApplicationCommandDeleteEvent
 */

@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. The deprecation level will be raised to HIDDEN in 0.16.0 and this declaration will be removed in " +
        "0.17.0.",
    level = DeprecationLevel.ERROR,
)
public class UnknownApplicationCommandDeleteEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandDeleteEvent
