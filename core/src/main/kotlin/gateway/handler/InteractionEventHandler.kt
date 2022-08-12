package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.remove
import dev.kord.core.Kord
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.GuildApplicationCommandPermissionsData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.application.*
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.interaction.*
import dev.kord.gateway.*
import dev.kord.core.event.Event as CoreEvent


public class InteractionEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord): CoreEvent? =
        when (event) {
            is InteractionCreate -> handle(event, shard, kord)
            is ApplicationCommandCreate -> handle(event, shard, kord)
            is ApplicationCommandUpdate -> handle(event, shard, kord)
            is ApplicationCommandDelete -> handle(event, shard, kord)
            is ApplicationCommandPermissionsUpdate -> {
                val data = GuildApplicationCommandPermissionsData.from(event.permissions)
                ApplicationCommandPermissionsUpdateEvent(
                    ApplicationCommandPermissions(data),
                    kord, shard,
                )
            }
            else -> null
        }

    private fun handle(event: InteractionCreate, shard: Int, kord: Kord): InteractionCreateEvent {
        val data = InteractionData.from(event.interaction)
        val coreEvent = when (val interaction = Interaction.from(data, kord)) {
            is GlobalAutoCompleteInteraction -> GlobalAutoCompleteInteractionCreateEvent(kord, shard, interaction)
            is GlobalChatInputCommandInteraction -> GlobalChatInputCommandInteractionCreateEvent(interaction, kord, shard)
            is GlobalUserCommandInteraction -> GlobalUserCommandInteractionCreateEvent(interaction, kord, shard)
            is GlobalMessageCommandInteraction -> GlobalMessageCommandInteractionCreateEvent(interaction, kord, shard)
            is GlobalButtonInteraction -> GlobalButtonInteractionCreateEvent(interaction, kord, shard)
            is GlobalSelectMenuInteraction -> GlobalSelectMenuInteractionCreateEvent(interaction, kord, shard)
            is GlobalModalSubmitInteraction -> GlobalModalSubmitInteractionCreateEvent(interaction, shard, kord)
            is GuildAutoCompleteInteraction -> GuildAutoCompleteInteractionCreateEvent(kord, shard, interaction)
            is GuildChatInputCommandInteraction -> GuildChatInputCommandInteractionCreateEvent(interaction, kord, shard)
            is GuildMessageCommandInteraction -> GuildMessageCommandInteractionCreateEvent(interaction, kord, shard)
            is GuildUserCommandInteraction -> GuildUserCommandInteractionCreateEvent(interaction, kord, shard)
            is GuildButtonInteraction -> GuildButtonInteractionCreateEvent(interaction, kord, shard)
            is GuildSelectMenuInteraction -> GuildSelectMenuInteractionCreateEvent(interaction, kord, shard)
            is GuildModalSubmitInteraction -> GuildModalSubmitInteractionCreateEvent(interaction, kord, shard)
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandCreate,
        shard: Int,
        kord: Kord,
    ): ApplicationCommandCreateEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.put(data)
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandCreateEvent(application, kord, shard)
            is GuildMessageCommand -> MessageCommandCreateEvent(application, kord, shard)
            is GuildUserCommand -> UserCommandCreateEvent(application, kord, shard)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandCreateEvent(application, kord, shard)
        }
        return coreEvent
    }


    private suspend fun handle(
        event: ApplicationCommandUpdate,
        shard: Int,
        kord: Kord,
    ): ApplicationCommandUpdateEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.put(data)

        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandUpdateEvent(application, kord, shard)
            is GuildMessageCommand -> MessageCommandUpdateEvent(application, kord, shard)
            is GuildUserCommand -> UserCommandUpdateEvent(application, kord, shard)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandUpdateEvent(application, kord, shard)
        }
        return coreEvent
    }

    private suspend fun handle(
        event: ApplicationCommandDelete,
        shard: Int,
        kord: Kord,
    ): ApplicationCommandDeleteEvent {
        val data = ApplicationCommandData.from(event.application)
        cache.remove<ApplicationCommandData> { idEq(ApplicationCommandData::id, data.id) }
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandDeleteEvent(application, kord, shard)
            is GuildMessageCommand -> MessageCommandDeleteEvent(application, kord, shard)
            is GuildUserCommand -> UserCommandDeleteEvent(application, kord, shard)
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandDeleteEvent(application, kord, shard)
        }
        return coreEvent
    }
}
