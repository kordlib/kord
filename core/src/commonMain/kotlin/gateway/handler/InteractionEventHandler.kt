package dev.kord.core.gateway.handler

import dev.kord.core.Kord
import dev.kord.core.cache.data.GuildApplicationCommandPermissionsData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.entity.interaction.*
import dev.kord.core.event.interaction.*
import dev.kord.gateway.ApplicationCommandPermissionsUpdate
import dev.kord.gateway.Event
import dev.kord.gateway.InteractionCreate
import dev.kord.core.event.Event as CoreEvent


internal class InteractionEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: LazyContext?): CoreEvent? =
        when (event) {
            is InteractionCreate -> handle(event, shard, kord, context)
            is ApplicationCommandPermissionsUpdate -> {
                val data = GuildApplicationCommandPermissionsData.from(event.permissions)
                ApplicationCommandPermissionsUpdateEvent(
                    ApplicationCommandPermissions(data),
                    kord, shard, context?.get(),
                )
            }

            else -> null
        }

    private suspend fun handle(
        event: InteractionCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?
    ): InteractionCreateEvent {
        val data = InteractionData.from(event.interaction)
        val coreEvent = when (val interaction = Interaction.from(data, kord)) {
            is GlobalAutoCompleteInteraction -> GlobalAutoCompleteInteractionCreateEvent(
                kord,
                shard,
                interaction,
                context?.get()
            )

            is GlobalChatInputCommandInteraction -> GlobalChatInputCommandInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )

            is GlobalUserCommandInteraction -> GlobalUserCommandInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )

            is GlobalMessageCommandInteraction -> GlobalMessageCommandInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )

            is GlobalButtonInteraction -> GlobalButtonInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GlobalSelectMenuInteraction -> GlobalSelectMenuInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )

            is GlobalModalSubmitInteraction -> GlobalModalSubmitInteractionCreateEvent(
                interaction,
                shard,
                kord,
                context?.get()
            )

            is GuildAutoCompleteInteraction -> GuildAutoCompleteInteractionCreateEvent(
                kord,
                shard,
                interaction,
                context?.get()
            )

            is GuildChatInputCommandInteraction -> GuildChatInputCommandInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )

            is GuildMessageCommandInteraction -> GuildMessageCommandInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )

            is GuildUserCommandInteraction -> GuildUserCommandInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )

            is GuildButtonInteraction -> GuildButtonInteractionCreateEvent(interaction, kord, shard, context?.get())
            is GuildSelectMenuInteraction -> GuildSelectMenuInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )

            is GuildModalSubmitInteraction -> GuildModalSubmitInteractionCreateEvent(
                interaction,
                kord,
                shard,
                context?.get()
            )
        }
        return coreEvent
    }
}
