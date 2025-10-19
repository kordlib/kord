package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.*
import dev.kord.core.event.Event


@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. This declaration will be removed in 0.17.0.",
    level = DeprecationLevel.HIDDEN,
)
public sealed interface ApplicationCommandCreateEvent : Event {
    public val command: GuildApplicationCommand
}

@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. This declaration will be removed in 0.17.0.",
    level = DeprecationLevel.HIDDEN,
)
public class ChatInputCommandCreateEvent(
    override val command: GuildChatInputCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent


@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. This declaration will be removed in 0.17.0.",
    level = DeprecationLevel.HIDDEN,
)
public class UserCommandCreateEvent(
    override val command: GuildUserCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent


@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. This declaration will be removed in 0.17.0.",
    level = DeprecationLevel.HIDDEN,
)
public class MessageCommandCreateEvent(
    override val command: GuildMessageCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent


@Suppress("DEPRECATION_ERROR")
@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details. This declaration will be removed in 0.17.0.",
    level = DeprecationLevel.HIDDEN,
)
public class UnknownApplicationCommandCreateEvent(
    override val command: UnknownGuildApplicationCommand,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : ApplicationCommandCreateEvent
