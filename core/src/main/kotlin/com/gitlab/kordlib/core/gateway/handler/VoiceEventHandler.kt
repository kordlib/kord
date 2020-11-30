package dev.kord.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.cache.api.query
import dev.kord.common.entity.optional.optional
import dev.kord.core.Kord
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.entity.VoiceState
import dev.kord.core.event.guild.VoiceServerUpdateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.Event
import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import dev.kord.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class VoiceEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreFlow: MutableSharedFlow<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is VoiceStateUpdate -> handle(event, shard)
        is VoiceServerUpdate -> handle(event, shard)
        else -> Unit
    }

    private suspend fun handle(event: VoiceStateUpdate, shard: Int) {
        val data = VoiceStateData.from(event.voiceState.guildId.value!!, event.voiceState)

        val old = cache.query<VoiceStateData> { idEq(VoiceStateData::id, data.id) }
                .asFlow().map { VoiceState(it, kord) }.singleOrNull()

        cache.put(data)
        val new = VoiceState(data, kord)

        coreFlow.emit( VoiceStateUpdateEvent(old, new, shard))
    }

    private suspend fun handle(event: VoiceServerUpdate, shard: Int) = with(event.voiceServerUpdateData) {
        coreFlow.emit(VoiceServerUpdateEvent(token, guildId, endpoint, kord, shard))
    }

}