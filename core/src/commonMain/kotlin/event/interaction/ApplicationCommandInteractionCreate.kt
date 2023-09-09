package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.Event

/** The [Event] dispatched when an [ApplicationCommandInteraction] is created. */
public sealed interface ApplicationCommandInteractionCreateEvent : ActionInteractionCreateEvent {
    override val interaction: ApplicationCommandInteraction
}


/** The [Event] dispatched when a [GlobalApplicationCommandInteraction] is created. */
public sealed interface GlobalApplicationCommandInteractionCreateEvent :
    ApplicationCommandInteractionCreateEvent {
    override val interaction: GlobalApplicationCommandInteraction
}

/** The [Event] dispatched when a [GuildApplicationCommandInteraction] is created. */
public sealed interface GuildApplicationCommandInteractionCreateEvent :
    ApplicationCommandInteractionCreateEvent {
    override val interaction: GuildApplicationCommandInteraction
}


/** The [Event] dispatched when a [UserCommandInteraction] is created. */
public sealed interface UserCommandInteractionCreateEvent : ApplicationCommandInteractionCreateEvent {
    override val interaction: UserCommandInteraction
}

/** The [Event] dispatched when a [GuildUserCommandInteraction] is created. */
public class GuildUserCommandInteractionCreateEvent(
    override val interaction: GuildUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : GuildApplicationCommandInteractionCreateEvent, UserCommandInteractionCreateEvent

/** The [Event] dispatched when a [GlobalUserCommandInteraction] is created. */
public class GlobalUserCommandInteractionCreateEvent(
    override val interaction: GlobalUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : GlobalApplicationCommandInteractionCreateEvent, UserCommandInteractionCreateEvent


/** The [Event] dispatched when a [MessageCommandInteraction] is created. */
public sealed interface MessageCommandInteractionCreateEvent : ApplicationCommandInteractionCreateEvent {
    override val interaction: MessageCommandInteraction
}

/** The [Event] dispatched when a [GuildMessageCommandInteraction] is created. */
public class GuildMessageCommandInteractionCreateEvent(
    override val interaction: GuildMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : GuildApplicationCommandInteractionCreateEvent, MessageCommandInteractionCreateEvent

/** The [Event] dispatched when a [GlobalMessageCommandInteraction] is created. */
public class GlobalMessageCommandInteractionCreateEvent(
    override val interaction: GlobalMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : GlobalApplicationCommandInteractionCreateEvent, MessageCommandInteractionCreateEvent


/** The [Event] dispatched when a [ChatInputCommandInteraction] is created. */
public sealed interface ChatInputCommandInteractionCreateEvent : ApplicationCommandInteractionCreateEvent {
    override val interaction: ChatInputCommandInteraction
}

/** The [Event] dispatched when a [GuildChatInputCommandInteraction] is created. */
public class GuildChatInputCommandInteractionCreateEvent(
    override val interaction: GuildChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : GuildApplicationCommandInteractionCreateEvent, ChatInputCommandInteractionCreateEvent

/** The [Event] dispatched when a [GlobalChatInputCommandInteraction] is created. */
public class GlobalChatInputCommandInteractionCreateEvent(
    override val interaction: GlobalChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : GlobalApplicationCommandInteractionCreateEvent, ChatInputCommandInteractionCreateEvent
