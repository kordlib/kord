package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event


@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public sealed interface ApplicationCommandDeleteEvent : Event {
    public val command: GuildApplicationCommand
}

@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public class ChatInputCommandDeleteEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandDeleteEvent


@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public class UserCommandDeleteEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandDeleteEvent


@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public class MessageCommandDeleteEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandDeleteEvent


@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public class UnknownApplicationCommandDeleteEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandDeleteEvent
