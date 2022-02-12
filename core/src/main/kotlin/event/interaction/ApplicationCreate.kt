package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.AutoCompleteInteractionBehavior
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
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

public sealed interface ApplicationInteractionCreateEvent : ActionInteractionCreateEvent {
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
    override val interaction: ChatInputCommandInteraction
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

public sealed interface ModalSubmitInteractionCreateEvent : ActionInteractionCreateEvent {
    override val interaction: ModalSubmitInteraction
}

public class GuildModalSubmitInteractionCreateEvent(
    override val interaction: GuildModalSubmitInteraction,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ModalSubmitInteractionCreateEvent, CoroutineScope by coroutineScope


public class GlobalModalSubmitInteractionCreateEvent(
    override val interaction: GlobalModalSubmitInteraction,
    override val shard: Int,
    override val kord: Kord,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : ModalSubmitInteractionCreateEvent, CoroutineScope by coroutineScope

/**
 * ActionInteraction received when a users types into an auto-completed option.
 *
 * Check [AutoCompleteInteractionBehavior] on how to reply.
 *
 * @see AutoCompleteInteraction
 */
public sealed interface AutoCompleteInteractionCreateEvent : DataInteractionCreateEvent {
    override val interaction: AutoCompleteInteraction
}

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
) : AutoCompleteInteractionCreateEvent, CoroutineScope by coroutineScope

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
) : AutoCompleteInteractionCreateEvent, CoroutineScope by coroutineScope
