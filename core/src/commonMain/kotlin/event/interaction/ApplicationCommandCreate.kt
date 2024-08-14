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

@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public sealed interface ApplicationCommandCreateEvent : Event {
    public val command: GuildApplicationCommand
}

/**
 * The event dispatched when a Chat Input command is created.
 *
 * @see ApplicationCommandCreateEvent
 */
@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
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

@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
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

@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
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

@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public class UnknownApplicationCommandCreateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent
