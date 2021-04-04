package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.channel.*
import dev.kord.core.event.channel.*
import dev.kord.core.event.channel.data.ChannelPinsUpdateEventData
import dev.kord.core.event.channel.data.TypingStartEventData
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.*
import kotlinx.coroutines.flow.MutableSharedFlow
import dev.kord.core.event.Event as CoreEvent

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
            else -> UnknownChannelCreateEvent(channel, shard)
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
            else -> UnknownChannelUpdateEvent(channel, shard)
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
            else -> UnknownChannelDeleteEvent(channel, shard)
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