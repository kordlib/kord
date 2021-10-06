package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.core.Kord
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.entity.VoiceState
import dev.kord.core.event.guild.VoiceServerUpdateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.gateway.Event
import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlin.coroutines.CoroutineContext
import dev.kord.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class VoiceEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? =
        when (event) {
            is VoiceStateUpdate -> handle(event, shard, kord, coroutineScope)
            is VoiceServerUpdate -> handle(event, shard, kord, coroutineScope)
            else -> null
        }

    private suspend fun handle(
        event: VoiceStateUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): VoiceStateUpdateEvent {
        val data = VoiceStateData.from(event.voiceState.guildId.value!!, event.voiceState)

        val old = cache.query<VoiceStateData> { idEq(VoiceStateData::id, data.id) }
            .asFlow().map { VoiceState(it, kord) }.singleOrNull()

        cache.put(data)
        val new = VoiceState(data, kord)

        return VoiceStateUpdateEvent(old, new, shard, coroutineScope)
    }

    private fun handle(
        event: VoiceServerUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): VoiceServerUpdateEvent =
        with(event.voiceServerUpdateData) {
            return VoiceServerUpdateEvent(token, guildId, endpoint, kord, shard, coroutineScope = coroutineScope)
        }

}