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
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.channel.*
import dev.kord.core.event.channel.data.ChannelPinsUpdateEventData
import dev.kord.core.event.channel.data.TypingStartEventData
import dev.kord.gateway.*
import kotlinx.coroutines.CoroutineScope
import dev.kord.core.event.Event as CoreEvent

internal class ChannelEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): dev.kord.core.event.Event? = when (event) {
        is ChannelCreate -> handle(event, shard, kord, coroutineScope)
        is ChannelUpdate -> handle(event, shard, kord, coroutineScope)
        is ChannelDelete -> handle(event, shard, kord, coroutineScope)
        is ChannelPinsUpdate -> handle(event, shard, kord, coroutineScope)
        is TypingStart -> handle(event, shard, kord, coroutineScope)
        else -> null
    }

    private suspend fun handle(event: ChannelCreate, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel, shard, coroutineScope)
            is StoreChannel -> StoreChannelCreateEvent(channel, shard, coroutineScope)
            is DmChannel -> DMChannelCreateEvent(channel, shard, coroutineScope)
            is TextChannel -> TextChannelCreateEvent(channel, shard, coroutineScope)
            is StageChannel -> StageChannelCreateEvent(channel, shard, coroutineScope)
            is VoiceChannel -> VoiceChannelCreateEvent(channel, shard, coroutineScope)
            is Category -> CategoryCreateEvent(channel, shard, coroutineScope)
            is ThreadChannel -> return null
            else -> UnknownChannelCreateEvent(channel, shard, coroutineScope)

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelUpdate, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        val data = ChannelData.from(event.channel)
        val oldData = cache.query<ChannelData> { idEq(ChannelData::id, data.id) }.singleOrNull()
        cache.put(data)
        val old = oldData?.let { Channel.from(it, kord) }
        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel, old as? NewsChannel, shard, coroutineScope)
            is StoreChannel -> StoreChannelUpdateEvent(channel, old as? StoreChannel, shard, coroutineScope)
            is DmChannel -> DMChannelUpdateEvent(channel, old as? DmChannel, shard, coroutineScope)
            is TextChannel -> TextChannelUpdateEvent(channel, old as? TextChannel, shard, coroutineScope)
            is StageChannel -> StageChannelUpdateEvent(channel, old as? StageChannel, shard, coroutineScope)
            is VoiceChannel -> VoiceChannelUpdateEvent(channel, old as? VoiceChannel, shard, coroutineScope)
            is Category -> CategoryUpdateEvent(channel, old as? Category, shard, coroutineScope)
            is ThreadChannel -> return null
            else -> UnknownChannelUpdateEvent(channel, old, shard, coroutineScope)

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelDelete, shard: Int, kord: Kord, coroutineScope: CoroutineScope): CoreEvent? {
        cache.remove<ChannelData> { idEq(ChannelData::id, event.channel.id) }
        val data = ChannelData.from(event.channel)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel, shard, coroutineScope)
            is StoreChannel -> StoreChannelDeleteEvent(channel, shard, coroutineScope)
            is DmChannel -> DMChannelDeleteEvent(channel, shard, coroutineScope)
            is TextChannel -> TextChannelDeleteEvent(channel, shard, coroutineScope)
            is StageChannel -> StageChannelDeleteEvent(channel, shard, coroutineScope)
            is VoiceChannel -> VoiceChannelDeleteEvent(channel, shard, coroutineScope)
            is Category -> CategoryDeleteEvent(channel, shard, coroutineScope)
            is ThreadChannel -> return null
            else -> UnknownChannelDeleteEvent(channel, shard, coroutineScope)
        }

        return coreEvent
    }

    private suspend fun handle(
        event: ChannelPinsUpdate,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): ChannelPinsUpdateEvent =
        with(event.pins) {
            val coreEvent = ChannelPinsUpdateEvent(
                ChannelPinsUpdateEventData.from(this),
                kord,
                shard,
                coroutineScope = coroutineScope
            )

            cache.query<ChannelData> { idEq(ChannelData::id, channelId) }.update {
                it.copy(lastPinTimestamp = lastPinTimestamp)
            }

            return coreEvent
        }

    private suspend fun handle(
        event: TypingStart,
        shard: Int,
        kord: Kord,
        coroutineScope: CoroutineScope
    ): TypingStartEvent = with(event.data) {
        member.value?.let {
            cache.put(MemberData.from(userId = it.user.value!!.id, guildId = guildId.value!!, it))
        }

        return TypingStartEvent(
            TypingStartEventData.from(this),
            kord,
            shard,
            coroutineScope = coroutineScope
        )
    }

}
