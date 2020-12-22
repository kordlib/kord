package dev.kord.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.entity.interaction.PartialInteraction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.interaction.PartialInteractionCreateEvent
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.Event
import dev.kord.gateway.InteractionCreate
import kotlinx.coroutines.flow.MutableSharedFlow
import dev.kord.core.event.Event as CoreEvent

class InteractionEventHandler(
    kord: Kord,
    gateway: MasterGateway,
    cache: DataCache,
    coreFlow: MutableSharedFlow<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {
    override suspend fun handle(event: Event, shard: Int) {

        if(event !is InteractionCreate) return
        val data = InteractionData.from(event)
        val partial = PartialInteraction(data, kord)
        coreFlow.emit(PartialInteractionCreateEvent(partial, kord, shard))

        if (kord.resources.applicationId != null) {
            val interaction = Interaction(data, kord.resources.applicationId, kord)
            coreFlow.emit(InteractionCreateEvent(interaction, kord, shard))
        }

    }
}