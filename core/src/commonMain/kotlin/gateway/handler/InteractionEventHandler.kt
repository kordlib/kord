package dev.kord.core.gateway.handler

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


internal class InteractionEventHandler : BaseGatewayEventHandler() {

    @Suppress("DEPRECATION_ERROR")
    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is InteractionCreate -> handle(event, shard, kord, context)
            is ApplicationCommandCreate -> handle(event, shard, kord, context)
            is ApplicationCommandUpdate -> handle(event, shard, kord, context)
            is ApplicationCommandDelete -> handle(event, shard, kord, context)
            is ApplicationCommandPermissionsUpdate -> {
                val data = GuildApplicationCommandPermissionsData.from(event.permissions)
                ApplicationCommandPermissionsUpdateEvent(
                    ApplicationCommandPermissions(data),
                    kord, shard, context?.get(),
                )
            }
            else -> null
        }

    private suspend fun handle(event: InteractionCreate, shard: Int, kord: Kord, context: LazyContext?): InteractionCreateEvent {
        val data = InteractionData.from(event.interaction)
        val coreEvent = when (val interaction = Interaction.from(data, kord)) {
            is GlobalAutoCompleteInteraction -> GlobalAutoCompleteInteractionCreateEvent(kord, shard, interaction, context?.get())
            is GlobalChatInputCommandInteraction -> GlobalChatInputCommandInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GlobalUserCommandInteraction -> GlobalUserCommandInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GlobalMessageCommandInteraction -> GlobalMessageCommandInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GlobalButtonInteraction -> GlobalButtonInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GlobalSelectMenuInteraction -> GlobalSelectMenuInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GlobalModalSubmitInteraction -> GlobalModalSubmitInteractionCreateEvent(interaction, shard, kord, context?.get())
            is GuildAutoCompleteInteraction -> GuildAutoCompleteInteractionCreateEvent(kord, shard, interaction, context?.get())
            is GuildChatInputCommandInteraction -> GuildChatInputCommandInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GuildMessageCommandInteraction -> GuildMessageCommandInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GuildUserCommandInteraction -> GuildUserCommandInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GuildButtonInteraction -> GuildButtonInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GuildSelectMenuInteraction -> GuildSelectMenuInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GuildModalSubmitInteraction -> GuildModalSubmitInteractionCreateEvent(interaction, kord, shard, context?.get())
        }
        return coreEvent
    }

    @Suppress("DEPRECATION_ERROR")
    private suspend fun handle(
        event: ApplicationCommandCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): ApplicationCommandCreateEvent {
        val data = ApplicationCommandData.from(event.application)
        kord.cache.put(data)
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandCreateEvent(application, kord, shard, context?.get())
            is GuildMessageCommand -> MessageCommandCreateEvent(application, kord, shard, context?.get())
            is GuildUserCommand -> UserCommandCreateEvent(application, kord, shard, context?.get())
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandCreateEvent(application, kord, shard, context?.get())
        }
        return coreEvent
    }


    @Suppress("DEPRECATION_ERROR")
    private suspend fun handle(
        event: ApplicationCommandUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): ApplicationCommandUpdateEvent {
        val data = ApplicationCommandData.from(event.application)
        kord.cache.put(data)

        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandUpdateEvent(application, kord, shard, context?.get())
            is GuildMessageCommand -> MessageCommandUpdateEvent(application, kord, shard, context?.get())
            is GuildUserCommand -> UserCommandUpdateEvent(application, kord, shard, context?.get())
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandUpdateEvent(application, kord, shard, context?.get())
        }
        return coreEvent
    }

    @Suppress("DEPRECATION_ERROR")
    private suspend fun handle(
        event: ApplicationCommandDelete,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): ApplicationCommandDeleteEvent {
        val data = ApplicationCommandData.from(event.application)
        kord.cache.remove { idEq(ApplicationCommandData::id, data.id) }
        val coreEvent = when (val application = GuildApplicationCommand(data, kord.rest.interaction)) {
            is GuildChatInputCommand -> ChatInputCommandDeleteEvent(application, kord, shard, context?.get())
            is GuildMessageCommand -> MessageCommandDeleteEvent(application, kord, shard, context?.get())
            is GuildUserCommand -> UserCommandDeleteEvent(application, kord, shard, context?.get())
            is UnknownGuildApplicationCommand -> UnknownApplicationCommandDeleteEvent(application, kord, shard, context?.get())
        }
        return coreEvent
    }
}
