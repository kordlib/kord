package dev.kord.core.gateway.handler

import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.core.Kord
import dev.kord.core.cache.data.VoiceStateData
import dev.kord.core.cache.data.id
import dev.kord.core.cache.idEq
import dev.kord.core.entity.VoiceState
import dev.kord.core.event.guild.VoiceServerUpdateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.gateway.Event
import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import dev.kord.core.event.Event as CoreEvent

internal class VoiceEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: LazyContext?): CoreEvent? = when (event) {
        is VoiceStateUpdate -> handle(event, shard, kord, context)
        is VoiceServerUpdate -> handle(event, shard, kord, context)
        else -> null
    }

    private suspend fun handle(event: VoiceStateUpdate, shard: Int, kord: Kord, context: LazyContext?): VoiceStateUpdateEvent {
        val data = VoiceStateData.from(event.voiceState.guildId.value!!, event.voiceState)

        val old = kord.cache.query<VoiceStateData> { idEq(VoiceStateData::id, data.id) }
            .asFlow().map { VoiceState(it, kord) }.singleOrNull()

        kord.cache.put(data)
        val new = VoiceState(data, kord)

        return VoiceStateUpdateEvent(old, new, shard, context?.get())
    }

    private suspend fun handle(event: VoiceServerUpdate, shard: Int, kord: Kord, context: LazyContext?): VoiceServerUpdateEvent =
        with(event.voiceServerUpdateData) { VoiceServerUpdateEvent(token, guildId, endpoint, kord, shard, context?.get()) }
}
