package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.VoiceState
import com.gitlab.kordlib.core.event.VoiceServerUpdateEvent
import com.gitlab.kordlib.core.event.VoiceStateUpdateEvent
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.VoiceServerUpdate
import com.gitlab.kordlib.gateway.VoiceStateUpdate
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class VoiceEventHandler(
        kord: Kord,
        gateway: Gateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event) = when (event) {
        is VoiceStateUpdate -> handle(event)
        is VoiceServerUpdate -> handle(event)
        else -> Unit
    }

    private suspend fun handle(event: VoiceStateUpdate) {
        val data = VoiceStateData.from(event.voiceState)

        val old = cache.find<VoiceStateData> { VoiceStateData::id eq data.id }
                .asFlow().map { VoiceState(it, kord) }.singleOrNull()

        cache.put(data)
        val new = VoiceState(data, kord)

        coreEventChannel.send(VoiceStateUpdateEvent(old, new))
    }

    private suspend fun handle(event: VoiceServerUpdate) = with(event.voiceServerUpdateData) {
        coreEventChannel.send(VoiceServerUpdateEvent(token, Snowflake(guildId), endpoint, kord))
    }

}