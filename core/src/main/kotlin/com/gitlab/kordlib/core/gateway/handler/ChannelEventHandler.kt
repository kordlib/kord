package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.cache.api.remove
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.cache.data.MemberData
import com.gitlab.kordlib.core.cache.idEq
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.channel.*
import com.gitlab.kordlib.core.event.channel.data.ChannelPinsUpdateEventData
import com.gitlab.kordlib.core.event.channel.data.TypingStartEventData
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.flow.MutableSharedFlow
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class ChannelEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreFlow: MutableSharedFlow<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is ChannelCreate -> handle(event, shard)
        is ChannelUpdate -> handle(event, shard)
        is ChannelDelete -> handle(event, shard)
        is ChannelPinsUpdate -> handle(event, shard)
        is TypingStart -> handle(event, shard)
        else -> Unit
    }

    private suspend fun handle(event: ChannelCreate, shard: Int) {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel, shard)
            is StoreChannel -> StoreChannelCreateEvent(channel, shard)
            is DmChannel -> DMChannelCreateEvent(channel, shard)
            is TextChannel -> TextChannelCreateEvent(channel, shard)
            is VoiceChannel -> VoiceChannelCreateEvent(channel, shard)
            is Category -> CategoryCreateEvent(channel, shard)
            else -> error("unknown channel: $channel")
        }

        coreFlow.emit(coreEvent)
    }

    private suspend fun handle(event: ChannelUpdate, shard: Int) {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel, shard)
            is StoreChannel -> StoreChannelUpdateEvent(channel, shard)
            is DmChannel -> DMChannelUpdateEvent(channel, shard)
            is TextChannel -> TextChannelUpdateEvent(channel, shard)
            is VoiceChannel -> VoiceChannelUpdateEvent(channel, shard)
            is Category -> CategoryUpdateEvent(channel, shard)
            else -> error("unknown channel: $channel")
        }

        coreFlow.emit(coreEvent)
    }

    private suspend fun handle(event: ChannelDelete, shard: Int) {
        cache.remove<ChannelData> { idEq(ChannelData::id, event.channel.id) }
        val data = ChannelData.from(event.channel)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel, shard)
            is StoreChannel -> StoreChannelDeleteEvent(channel, shard)
            is DmChannel -> DMChannelDeleteEvent(channel, shard)
            is TextChannel -> TextChannelDeleteEvent(channel, shard)
            is VoiceChannel -> VoiceChannelDeleteEvent(channel, shard)
            is Category -> CategoryDeleteEvent(channel, shard)
            else -> error("unknown channel: $channel")
        }

        coreFlow.emit(coreEvent)
    }

    private suspend fun handle(event: ChannelPinsUpdate, shard: Int) = with(event.pins) {
        val coreEvent = ChannelPinsUpdateEvent(ChannelPinsUpdateEventData.from(this), kord, shard)

        cache.query<ChannelData> { idEq(ChannelData::id, channelId) }.update {
            it.copy(lastPinTimestamp = lastPinTimestamp)
        }

        coreFlow.emit(coreEvent)
    }

    private suspend fun handle(event: TypingStart, shard: Int) = with(event.data) {
        member.value?.let {
            cache.put(MemberData.from(userId = it.user.value!!.id, guildId = guildId.value!!, it))
        }

        val coreEvent = TypingStartEvent(
                TypingStartEventData.from(this),
                kord,
                shard
        )

        coreFlow.emit(coreEvent)
    }

}