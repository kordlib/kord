package dev.kord.core.event.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.*
import dev.kord.core.entity.application.*
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.Event

/**
 * This event fires when an interaction is created.
 *
 *
 * Discord currently has one type of interaction,
 * [Slash Commands][dev.kord.core.entity.interaction.ApplicationCommand].
 *
 * The event should be acknowledged  withing 3 seconds of reception using one of the following methods:
 * * [acknowledgeEphemeral][Interaction.acknowledgeEphemeral] - acknowledges an interaction ephemerally.
 * * [acknowledgePublic][Interaction.acknowledgePublic] - acknowledges an interaction in public.
 * * [respondPublic][Interaction.respondPublic] - same as public acknowledgement, but an immediate result (message) can be supplied.
 * * [respondEphemeral][Interaction.respondEphemeral] - same as ephemeral acknowledgement, but an immediate result (message) can be supplied.
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

sealed interface ApplicationCreateEvent : Event {
    val interaction: ApplicationCommandInteraction
}

sealed interface GlobalApplicationCreateEvent : ApplicationCreateEvent {
    override val interaction: GlobalApplicationCommandInteraction
}

sealed interface GuildApplicationCreateEvent : ApplicationCreateEvent {
    override val interaction: GuildApplicationCommandInteraction
}

sealed interface  UserCommandInteractionCreateEvent : ApplicationCreateEvent {
    override val interaction: UserCommandInteraction
}

class GuildUserCommandInteractionCreateEvent(
    override val interaction: GuildUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int
) : GuildApplicationCreateEvent, UserCommandInteractionCreateEvent

class GlobalUserCommandInteractionCreateEvent(
    override val interaction: GlobalUserCommandInteraction,
    override val kord: Kord,
    override val shard: Int
) : GlobalApplicationCreateEvent, UserCommandInteractionCreateEvent


sealed interface  MessageCommandInteractionCreateEvent : ApplicationCreateEvent {
    override val interaction: MessageCommandInteraction
}

class GuildMessageCommandInteractionCreateEvent(
    override val interaction: GuildMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int
) : GuildApplicationCreateEvent, MessageCommandInteractionCreateEvent

class GlobalMessageCommandInteractionCreateEvent(
    override val interaction: GlobalMessageCommandInteraction,
    override val kord: Kord,
    override val shard: Int
) : GlobalApplicationCreateEvent, MessageCommandInteractionCreateEvent



sealed interface  ChatInputCommandInteractionCreateEvent : ApplicationCreateEvent {
    override val interaction: ChatInputCommandInteraction
}

class GuildChatInputCommandInteractionCreateEvent(
    override val interaction: GuildChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int
) : GuildApplicationCreateEvent, ChatInputCommandInteractionCreateEvent

class GlobalChatInputCommandInteractionCreateEvent(
    override val interaction: GlobalChatInputCommandInteraction,
    override val kord: Kord,
    override val shard: Int
) : GlobalApplicationCreateEvent, ChatInputCommandInteractionCreateEvent
