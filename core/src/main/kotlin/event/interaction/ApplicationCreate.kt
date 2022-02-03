package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.*
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope

/**
 * This event fires when an interaction is created.
 *
 *
 * Discord currently has one type of interaction,
 * [Slash Commands][ApplicationCommand].
 *
 * The event should be acknowledged  withing 3 seconds of reception using one of the following methods:
 * * [acknowledgeEphemeral][ActionInteraction.acknowledgeEphemeral] - acknowledges an interaction ephemerally.
 * * [acknowledgePublic][ActionInteraction.acknowledgePublic] - acknowledges an interaction in public.
 * * [respondPublic][ActionInteraction.respondPublic] - same as public acknowledgement, but an immediate result (message) can be supplied.
 * * [respondEphemeral][ActionInteraction.respondEphemeral] - same as ephemeral acknowledgement, but an immediate result (message) can be supplied.
 *
 * Once an interaction has been acknowledged,
 * you can use [PublicInteractionResponseBehavior.followUp] or [EphemeralInteractionResponseBehavior.followUp] to display additional messages.
 *
 * The resulting followup message and its methods may differ based on which method is used.
 * * Following up an acknowledgement results in replacing "The bot is thinking" prompt with the followup content.
 * * Following up a response results in a completely new message instance.
 *
 * As such, due to how Discord handles ephemeral acknowledgements,
 * a followup on ephemeral acknowledgement will result in an ephemeral message.
 *
 * In the current iteration, ephemeral messages (regardless of the type) don't support files and/or embeds.
 */

public sealed interface ApplicationInteractionCreateEvent<out I : ApplicationCommandInvocationInteraction> :
    ActionInteractionCreateEvent<I> {
    override val interaction: I
}

public sealed interface GlobalApplicationInteractionCreateEvent<out I : GlobalApplicationCommandInteraction> :
    ApplicationInteractionCreateEvent<I> {
    override val interaction: I
}

public sealed interface GuildApplicationInteractionCreateEvent<out I : GuildApplicationCommandInteraction> :
    ApplicationInteractionCreateEvent<I> {
    override val interaction: I
}

public sealed interface UserCommandInteractionCreateEvent<out I : UserCommandInteraction> :
    ApplicationInteractionCreateEvent<I> {
    override val interaction: I
}

public class GuildUserCommandInteractionCreateEvent(
    override val interaction: GuildUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationInteractionCreateEvent<GuildUserCommandInteraction>,
    UserCommandInteractionCreateEvent<GuildUserCommandInteraction>,
    CoroutineScope by coroutineScope

public class GlobalUserCommandInteractionCreateEvent(
    override val interaction: GlobalUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationInteractionCreateEvent<GlobalUserCommandInteraction>,
    UserCommandInteractionCreateEvent<GlobalUserCommandInteraction>,
    CoroutineScope by coroutineScope


public sealed interface MessageCommandInteractionCreateEvent<out I : MessageCommandInteraction> :
    ApplicationInteractionCreateEvent<I> {
    override val interaction: I
}

public class GuildMessageCommandInteractionCreateEvent(
    override val interaction: GuildMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationInteractionCreateEvent<GuildMessageCommandInteraction>,
    MessageCommandInteractionCreateEvent<GuildMessageCommandInteraction>,
    CoroutineScope by coroutineScope

public class GlobalMessageCommandInteractionCreateEvent(
    override val interaction: GlobalMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationInteractionCreateEvent<GlobalMessageCommandInteraction>,
    MessageCommandInteractionCreateEvent<GlobalMessageCommandInteraction>,
    CoroutineScope by coroutineScope


public sealed interface ChatInputCommandInteractionCreateEvent<out I : ChatInputCommandInvocationInteraction> :
    ApplicationInteractionCreateEvent<I> {
    override val interaction: I
}

public class GuildChatInputCommandInteractionCreateEvent(
    override val interaction: GuildChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationInteractionCreateEvent<GuildChatInputCommandInteraction>,
    ChatInputCommandInteractionCreateEvent<GuildChatInputCommandInteraction>,
    CoroutineScope by coroutineScope

public class GlobalChatInputCommandInteractionCreateEvent(
    override val interaction: GlobalChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationInteractionCreateEvent<GlobalChatInputCommandInteraction>,
    ChatInputCommandInteractionCreateEvent<GlobalChatInputCommandInteraction>,
    CoroutineScope by coroutineScope

/**
 * ActionInteraction received when a users types into an auto-completed option.
 *
 * Check [AutoCompleteInteractionBehavior] on how to reply.
 *
 * @see AutoCompleteInteraction
 */
public sealed interface AutoCompleteInteractionCreateEvent<out I : AutoCompleteInteraction> :
    DataInteractionCreateEvent<I> {
    override val interaction: I
}

/**
 * ActionInteraction received when a users types into an auto-completed option.
 *
 * Check [AutoCompleteInteractionBehavior] on how to reply.
 *
 * @see AutoCompleteInteraction
 */
public class GlobalAutoCompleteInteractionCreateEvent(
    override val kord: Kord,
    override val shard: Int,
    override val interaction: GlobalAutoCompleteInteraction,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : AutoCompleteInteractionCreateEvent<GlobalAutoCompleteInteraction>, CoroutineScope by coroutineScope

/**
 * ActionInteraction received when a users types into an auto-completed option.
 *
 * Check [AutoCompleteInteractionBehavior] on how to reply.
 *
 * @see AutoCompleteInteraction
 */
public class GuildAutoCompleteInteractionCreateEvent(
    override val kord: Kord,
    override val shard: Int,
    override val interaction: GuildAutoCompleteInteraction,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : AutoCompleteInteractionCreateEvent<GuildAutoCompleteInteraction>, CoroutineScope by coroutineScope
