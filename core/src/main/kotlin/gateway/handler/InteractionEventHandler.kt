package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.remove
import dev.kord.core.Kord
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.application.*
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.interaction.*
import dev.kord.gateway.*
import kotlin.coroutines.CoroutineContext
import dev.kord.core.event.Event as CoreEvent


public class InteractionEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: CoroutineContext): CoreEvent? =
        when (event) {
            is InteractionCreate -> handle(event, shard, kord, context)
            is ApplicationCommandCreate -> handle(event, shard, kord, context)
            is ApplicationCommandUpdate -> handle(event, shard, kord, context)
            is ApplicationCommandDelete -> handle(event, shard, kord, context)
            else -> null
        }

    private fun handle(event: InteractionCreate, shard: Int, kord: Kord, context: CoroutineContext): CoreEvent {
        val data = InteractionData.from(event.interaction)
        val interaction = Interaction.from(data, kord)
        val coreEvent = when(interaction) {
            is GlobalChatInputCommandInteraction -> GlobalChatInputCommandInteractionCreateEvent(interaction, kord, shard, context)
            is GlobalUserCommandInteraction -> GlobalUserCommandInteractionCreateEvent(interaction, kord, shard, context)
            is GlobalMessageCommandInteraction -> GlobalMessageCommandInteractionCreateEvent(interaction, kord, shard, context)
            is GlobalButtonInteraction -> GlobalButtonInteractionCreateEvent(interaction, kord, shard, context)
            is GlobalSelectMenuInteraction -> GlobalSelectMenuInteractionCreateEvent(interaction, kord, shard, context)
            is GuildChatInputCommandInteraction -> GuildChatInputCommandInteractionCreateEvent(interaction, kord, shard, context)
            is GuildMessageCommandInteraction -> GuildMessageCommandInteractionCreateEvent(interaction, kord, shard, context)
            is GuildUserCommandInteraction -> GuildUserCommandInteractionCreateEvent(interaction, kord, shard, context)
            is GuildButtonInteraction -> GuildButtonInteractionCreateEvent(interaction, kord, shard, context)
            is GuildSelectMenuInteraction -> GuildSelectMenuInteractionCreateEvent(interaction, kord, shard, context)
            is UnknownComponentInteraction -> error("Unknown component.")
            is UnknownApplicationCommandInteraction -> error("Unknown component.")
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandCreate,
        shard: Int,
        kord: Kord,
        context: CoroutineContext
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.put(data)
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandCreateEvent(application, kord, shard, context)
            is GuildMessageCommand -> MessageCommandCreateEvent(application, kord, shard, context)
            is GuildUserCommand -> UserCommandCreateEvent(application, kord, shard, context)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandCreateEvent(application, kord, shard, context)
        }
        return coreEvent
    }


    private suspend fun handle(
        event: ApplicationCommandUpdate,
        shard: Int,
        kord: Kord,
        context: CoroutineContext
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.put(data)
        val application = GuildApplicationCommand(data, kord.rest.interaction)

        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandUpdateEvent(application, kord, shard, context)
            is GuildMessageCommand -> MessageCommandUpdateEvent(application, kord, shard, context)
            is GuildUserCommand -> UserCommandUpdateEvent(application, kord, shard, context)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandUpdateEvent(application, kord, shard, context)
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandDelete,
        shard: Int,
        kord: Kord,
        context: CoroutineContext
    ): CoreEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.remove<ApplicationCommandData> { idEq(ApplicationCommandData::id, data.id) }
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandDeleteEvent(application, kord, shard, context)
            is GuildMessageCommand -> MessageCommandDeleteEvent(application, kord, shard, context)
            is GuildUserCommand -> UserCommandDeleteEvent(application, kord, shard, context)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandDeleteEvent(application, kord, shard, context)
        }
        return coreEvent
    }
}
