package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.remove
import dev.kord.core.Kord
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.GuildApplicationCommandPermissionData
import dev.kord.core.cache.data.GuildApplicationCommandPermissionsData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.application.*
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.interaction.*
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
            is ApplicationCommandPermissionsUpdate -> {
                val data = GuildApplicationCommandPermissionsData.from(event.permissions)
                ApplicationCommandPermissionsUpdateEvent(
                    ApplicationCommandPermissions(data),
                    kord, shard, coroutineScope
                )
            }
            else -> null
        }

    private fun handle(event: InteractionCreate, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent {
        val data = InteractionData.from(event.interaction)
        val coreEvent = when(val interaction = Interaction.from(data, kord)) {
            is GlobalAutoCompleteInteraction -> GlobalAutoCompleteInteractionCreateEvent(kord, shard, interaction, coroutineScope)
            is GlobalChatInputCommandInteraction -> GlobalChatInputCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalUserCommandInteraction -> GlobalUserCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalMessageCommandInteraction -> GlobalMessageCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalButtonInteraction -> GlobalButtonInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalSelectMenuInteraction -> GlobalSelectMenuInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GlobalModalSubmitInteraction -> GlobalModalSubmitInteractionCreateEvent(interaction, shard, kord, coroutineScope)
            is GuildAutoCompleteInteraction -> GuildAutoCompleteInteractionCreateEvent(kord, shard, interaction, coroutineScope)
            is GuildChatInputCommandInteraction -> GuildChatInputCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildMessageCommandInteraction -> GuildMessageCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildUserCommandInteraction -> GuildUserCommandInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildButtonInteraction -> GuildButtonInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildSelectMenuInteraction -> GuildSelectMenuInteractionCreateEvent(interaction, kord, shard, coroutineScope)
            is GuildModalSubmitInteraction -> GuildModalSubmitInteractionCreateEvent(interaction, kord, shard, coroutineScope)
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
