package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.*
import dev.kord.core.entity.application.*
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope

/**
 * This event fires when an interaction is created.
 *
 *
 * Discord currently has one type of interaction,
 * [Slash Commands][dev.kord.core.entity.interaction.ApplicationCommand].
 *
 * The event should be acknowledged  withing 3 seconds of reception using one of the following methods:
 * * [acknowledgeEphemeral][MessageRespondingInteraction.acknowledgeEphemeral] - acknowledges an interaction ephemerally.
 * * [acknowledgePublic][MessageRespondingInteraction.acknowledgePublic] - acknowledges an interaction in public.
 * * [respondPublic][MessageRespondingInteraction.respondPublic] - same as public acknowledgement, but an immediate result (message) can be supplied.
 * * [respondEphemeral][MessageRespondingInteraction.respondEphemeral] - same as ephemeral acknowledgement, but an immediate result (message) can be supplied.
 *
 * Once an interaction has been acknowledged,
 * you can use [PublicInteractionResponseBehavior.followUp] or [EphemeralInteractionResponseBehavior.followUp] to display additional messages.
 *
 * The resulting follow-up message and its methods may differ based on which method is used.
 * * Following up an acknowledgement results in replacing "The bot is thinking" prompt with the follow-up content.
 * * Following up a response results in a completely new message instance.
 *
 * As such, due to how Discord handles ephemeral acknowledgements,
 * a follow-up on ephemeral acknowledgement will result in an ephemeral message.
 *
 * In the current iteration, ephemeral messages (regardless of the type) don't support files and/or embeds.
 */

public sealed interface ApplicationInteractionCreateEvent : InteractionCreateEvent {
    override val interaction: ApplicationCommandInteraction
}

public sealed interface GlobalApplicationInteractionCreateEvent : ApplicationInteractionCreateEvent {
    override val interaction: GlobalApplicationCommandInteraction
}

public sealed interface GuildApplicationInteractionCreateEvent : ApplicationInteractionCreateEvent {
    override val interaction: GuildApplicationCommandInteraction
}

public sealed interface UserCommandInteractionCreateEvent : ApplicationInteractionCreateEvent {
    override val interaction: UserCommandInteraction
}

public class GuildUserCommandInteractionCreateEvent(
    override val interaction: GuildUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationInteractionCreateEvent, UserCommandInteractionCreateEvent, CoroutineScope by coroutineScope

public class GlobalUserCommandInteractionCreateEvent(
    override val interaction: GlobalUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationInteractionCreateEvent, UserCommandInteractionCreateEvent, CoroutineScope by coroutineScope


public sealed interface MessageCommandInteractionCreateEvent : ApplicationInteractionCreateEvent {
    override val interaction: MessageCommandInteraction
}

public class GuildMessageCommandInteractionCreateEvent(
    override val interaction: GuildMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationInteractionCreateEvent, MessageCommandInteractionCreateEvent, CoroutineScope by coroutineScope

public class GlobalMessageCommandInteractionCreateEvent(
    override val interaction: GlobalMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationInteractionCreateEvent, MessageCommandInteractionCreateEvent, CoroutineScope by coroutineScope


public sealed interface ChatInputCommandInteractionCreateEvent : ApplicationInteractionCreateEvent {
    override val interaction: ChatInputCommandInvocationInteraction
}

public class GuildChatInputCommandInteractionCreateEvent(
    override val interaction: GuildChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GuildApplicationInteractionCreateEvent, ChatInputCommandInteractionCreateEvent, CoroutineScope by coroutineScope

public class GlobalChatInputCommandInteractionCreateEvent(
    override val interaction: GlobalChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : GlobalApplicationInteractionCreateEvent, ChatInputCommandInteractionCreateEvent, CoroutineScope by coroutineScope

/**
 * MessageRespondingInteraction received when a users types into an auto-completed option.
 *
 * Check [AutoCompleteInteractionBehavior] on how to reply.
 *
 * @see AutoCompleteInteraction
 */
public sealed interface AutoCompleteInteractionCreateEvent : InteractionCreateEvent

internal fun AutoCompleteInteractionCreateEvent(
    interaction: AutoCompleteInteraction,
    kord: Kord,
    shard: Int,
    coroutineScope: CoroutineScope = kordCoroutineScope(kord)
): AutoCompleteInteractionCreateEvent = when (interaction) {
    is GuildAutoCompleteInteraction -> GuildAutoCompleteInteractionCreateEvent(
        kord, shard, interaction, coroutineScope
    )
    else -> GlobalAutoCompleteInteractionCreateEvent(
        kord, shard, interaction as GlobalAutoCompleteInteraction, coroutineScope
    )
}

/**
 * MessageRespondingInteraction received when a users types into an auto-completed option.
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
) : AutoCompleteInteractionCreateEvent, CoroutineScope by coroutineScope

/**
 * MessageRespondingInteraction received when a users types into an auto-completed option.
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
) : AutoCompleteInteractionCreateEvent, CoroutineScope by coroutineScope
