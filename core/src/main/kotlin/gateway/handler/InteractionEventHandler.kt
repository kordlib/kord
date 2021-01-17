package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.event.interaction.*
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.*
import kotlinx.coroutines.flow.MutableSharedFlow
import dev.kord.core.event.Event as CoreEvent

class InteractionEventHandler(
    kord: Kord,
    gateway: MasterGateway,
    cache: DataCache,
    coreFlow: MutableSharedFlow<CoreEvent>,
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {
    override suspend fun handle(event: Event, shard: Int) {
        when (event) {
            is InteractionCreate -> handle(event, shard)
            is ApplicationCommandCreate -> handle(event, shard)
            is ApplicationCommandUpdate -> handle(event, shard)
            is ApplicationCommandDelete -> handle(event, shard)
            else -> Unit
        }

    }

    private suspend fun handle(event: InteractionCreate, shard: Int) {
        val data = InteractionData.from(event)
        val interaction = Interaction(data, kord.selfId, kord)
        coreFlow.emit(InteractionCreateEvent(interaction, kord, shard))
    }

    private suspend fun handle(event: ApplicationCommandCreate, shard: Int) {
        val data = ApplicationCommandData.from(event.application)
        val application = GuildApplicationCommand(data, data.guildId.value!!, kord.rest.interaction)
        coreFlow.emit(ApplicationCommandCreateEvent(application, kord, shard))
    }


    private suspend fun handle(event: ApplicationCommandUpdate, shard: Int) {
        val data = ApplicationCommandData.from(event.application)
        val application = GuildApplicationCommand(data, data.guildId.value!!, kord.rest.interaction)
        coreFlow.emit(ApplicationCommandUpdateEvent(application, kord, shard))
    }

    private suspend fun handle(event: ApplicationCommandDelete, shard: Int) {
        val data = ApplicationCommandData.from(event.application)
        val application = GuildApplicationCommand(data, data.guildId.value!!, kord.rest.interaction)
        coreFlow.emit(ApplicationCommandDeleteEvent(application, kord, shard))
    }
}