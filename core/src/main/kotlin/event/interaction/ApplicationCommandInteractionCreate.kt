package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope

/** An [Event] that fires when an [ApplicationCommandInteraction] is created. */
public sealed interface ApplicationCommandInteractionCreateEvent : ActionInteractionCreateEvent {
    override val interaction: ApplicationCommandInteraction
}


/** An [Event] that fires when a [GlobalApplicationCommandInteraction] is created. */
public sealed interface GlobalApplicationCommandInteractionCreateEvent :
    ApplicationCommandInteractionCreateEvent {
    override val interaction: GlobalApplicationCommandInteraction
}

/** An [Event] that fires when a [GuildApplicationCommandInteraction] is created. */
public sealed interface GuildApplicationCommandInteractionCreateEvent :
    ApplicationCommandInteractionCreateEvent {
    override val interaction: GuildApplicationCommandInteraction
}


/** An [Event] that fires when a [UserCommandInteraction] is created. */
public sealed interface UserCommandInteractionCreateEvent : ApplicationCommandInteractionCreateEvent {
    override val interaction: UserCommandInteraction
}

/** An [Event] that fires when a [GuildUserCommandInteraction] is created. */
public class GuildUserCommandInteractionCreateEvent(
    override val interaction: GuildUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationCommandInteractionCreateEvent, UserCommandInteractionCreateEvent, CoroutineScope by coroutineScope

/** An [Event] that fires when a [GlobalUserCommandInteraction] is created. */
public class GlobalUserCommandInteractionCreateEvent(
    override val interaction: GlobalUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationCommandInteractionCreateEvent, UserCommandInteractionCreateEvent, CoroutineScope by coroutineScope


/** An [Event] that fires when a [MessageCommandInteraction] is created. */
public sealed interface MessageCommandInteractionCreateEvent : ApplicationCommandInteractionCreateEvent {
    override val interaction: MessageCommandInteraction
}

/** An [Event] that fires when a [GuildMessageCommandInteraction] is created. */
public class GuildMessageCommandInteractionCreateEvent(
    override val interaction: GuildMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationCommandInteractionCreateEvent,
    MessageCommandInteractionCreateEvent,
    CoroutineScope by coroutineScope

/** An [Event] that fires when a [GlobalMessageCommandInteraction] is created. */
public class GlobalMessageCommandInteractionCreateEvent(
    override val interaction: GlobalMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationCommandInteractionCreateEvent,
    MessageCommandInteractionCreateEvent,
    CoroutineScope by coroutineScope


/** An [Event] that fires when a [ChatInputCommandInteraction] is created. */
public sealed interface ChatInputCommandInteractionCreateEvent : ApplicationCommandInteractionCreateEvent {
    override val interaction: ChatInputCommandInteraction
}

/** An [Event] that fires when a [GuildChatInputCommandInteraction] is created. */
public class GuildChatInputCommandInteractionCreateEvent(
    override val interaction: GuildChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationCommandInteractionCreateEvent,
    ChatInputCommandInteractionCreateEvent,
    CoroutineScope by coroutineScope

/** An [Event] that fires when a [GlobalChatInputCommandInteraction] is created. */
public class GlobalChatInputCommandInteractionCreateEvent(
    override val interaction: GlobalChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationCommandInteractionCreateEvent,
    ChatInputCommandInteractionCreateEvent,
    CoroutineScope by coroutineScope
