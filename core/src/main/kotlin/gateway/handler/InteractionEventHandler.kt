package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.remove
import dev.kord.core.Kord
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.application.GuildApplicationCommand
import dev.kord.core.entity.application.GuildChatInputCommand
import dev.kord.core.entity.application.GuildMessageCommand
import dev.kord.core.entity.application.GuildUserCommand
import dev.kord.core.entity.application.UnknownGuildApplicationCommand
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import dev.kord.core.entity.interaction.GlobalButtonInteraction
import dev.kord.core.entity.interaction.GlobalChatInputCommandInteraction
import dev.kord.core.entity.interaction.GlobalMessageCommandInteraction
import dev.kord.core.entity.interaction.GlobalSelectMenuInteraction
import dev.kord.core.entity.interaction.GlobalUserCommandInteraction
import dev.kord.core.entity.interaction.GuildButtonInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.GuildMessageCommandInteraction
import dev.kord.core.entity.interaction.GuildSelectMenuInteraction
import dev.kord.core.entity.interaction.GuildUserCommandInteraction
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.entity.interaction.UnknownApplicationCommandInteraction
import dev.kord.core.entity.interaction.UnknownComponentInteraction
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.ChatInputCommandCreateEvent
import dev.kord.core.event.interaction.ChatInputCommandDeleteEvent
import dev.kord.core.event.interaction.ChatInputCommandUpdateEvent
import dev.kord.core.event.interaction.GlobalButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GlobalChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GlobalMessageCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GlobalSelectMenuInteractionCreateEvent
import dev.kord.core.event.interaction.GlobalUserCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildSelectMenuInteractionCreateEvent
import dev.kord.core.event.interaction.GuildUserCommandInteractionCreateEvent
import dev.kord.core.event.interaction.MessageCommandCreateEvent
import dev.kord.core.event.interaction.MessageCommandDeleteEvent
import dev.kord.core.event.interaction.MessageCommandUpdateEvent
import dev.kord.core.event.interaction.UnknownApplicationCommandCreateEvent
import dev.kord.core.event.interaction.UnknownApplicationCommandDeleteEvent
import dev.kord.core.event.interaction.UnknownApplicationCommandUpdateEvent
import dev.kord.core.event.interaction.UserCommandCreateEvent
import dev.kord.core.event.interaction.UserCommandDeleteEvent
import dev.kord.core.event.interaction.UserCommandUpdateEvent
import dev.kord.gateway.*
import kotlinx.coroutines.CoroutineScope
import dev.kord.core.event.Event as CoreEvent


public class InteractionEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? =
        when (event) {
            is InteractionCreate -> handle(event, shard, kord, coroutineScope)
            is ApplicationCommandCreate -> handle(event, shard, kord, coroutineScope)
            is ApplicationCommandUpdate -> handle(event, shard, kord, coroutineScope)
            is ApplicationCommandDelete -> handle(event, shard, kord, coroutineScope)
            else -> null
        }

    private fun handle(event: InteractionCreate, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent {
        val data = InteractionData.from(event.interaction)
        val interaction = Interaction.from(data, kord)
        val coreEvent = when(interaction) {
            is AutoCompleteInteraction -> AutoCompleteInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalChatInputCommandInteraction -> GlobalChatInputCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalUserCommandInteraction -> GlobalUserCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalMessageCommandInteraction -> GlobalMessageCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalButtonInteraction -> GlobalButtonInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalSelectMenuInteraction -> GlobalSelectMenuInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildChatInputCommandInteraction -> GuildChatInputCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildMessageCommandInteraction -> GuildMessageCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildUserCommandInteraction -> GuildUserCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildButtonInteraction -> GuildButtonInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildSelectMenuInteraction -> GuildSelectMenuInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is UnknownComponentInteraction -> error("Unknown component.")
            is UnknownApplicationCommandInteraction -> error("Unknown component.")
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandCreate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.put(data)
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandCreateEvent(application, kord, shard, coroutineScope)
            is GuildMessageCommand -> MessageCommandCreateEvent(application, kord, shard, coroutineScope)
            is GuildUserCommand -> UserCommandCreateEvent(application, kord, shard, coroutineScope)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandCreateEvent(application, kord, shard, coroutineScope)
        }
        return coreEvent
    }


    private suspend fun handle(
        event: ApplicationCommandUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.put(data)

        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandUpdateEvent(application, kord, shard, coroutineScope)
            is GuildMessageCommand -> MessageCommandUpdateEvent(application, kord, shard, coroutineScope)
            is GuildUserCommand -> UserCommandUpdateEvent(application, kord, shard, coroutineScope)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandUpdateEvent(application, kord, shard, coroutineScope)
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandDelete,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.remove<ApplicationCommandData> { idEq(ApplicationCommandData::id, data.id) }
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandDeleteEvent(application, kord, shard, coroutineScope)
            is GuildMessageCommand -> MessageCommandDeleteEvent(application, kord, shard, coroutineScope)
            is GuildUserCommand -> UserCommandDeleteEvent(application, kord, shard, coroutineScope)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandDeleteEvent(application, kord, shard, coroutineScope)
        }
        return coreEvent
    }
}
