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
import kotlin.coroutines.CoroutineContext
import dev.kord.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class ChannelEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
        context: CoroutineContext
    ): dev.kord.core.event.Event? = when (event) {
        is ChannelCreate -> handle(event, shard, kord, context)
        is ChannelUpdate -> handle(event, shard, kord, context)
        is ChannelDelete -> handle(event, shard, kord, context)
        is ChannelPinsUpdate -> handle(event, shard, kord, context)
        is TypingStart -> handle(event, shard, kord, context)
        else -> null
    }

    private suspend fun handle(event: ChannelCreate, shard: Int, kord: Kord, context: CoroutineContext): CoreEvent? {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel, shard, context)
            is StoreChannel -> StoreChannelCreateEvent(channel, shard, context)
            is DmChannel -> DMChannelCreateEvent(channel, shard, context)
            is TextChannel -> TextChannelCreateEvent(channel, shard, context)
            is StageChannel -> StageChannelCreateEvent(channel, shard, context)
            is VoiceChannel -> VoiceChannelCreateEvent(channel, shard, context)
            is Category -> CategoryCreateEvent(channel, shard, context)
            is ThreadChannel -> return null
            else -> UnknownChannelCreateEvent(channel, shard, context)

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelUpdate, shard: Int, kord: Kord, context: CoroutineContext): CoreEvent? {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel, shard, context)
            is StoreChannel -> StoreChannelUpdateEvent(channel, shard, context)
            is DmChannel -> DMChannelUpdateEvent(channel, shard, context)
            is TextChannel -> TextChannelUpdateEvent(channel, shard, context)
            is StageChannel -> StageChannelUpdateEvent(channel, shard, context)
            is VoiceChannel -> VoiceChannelUpdateEvent(channel, shard, context)
            is Category -> CategoryUpdateEvent(channel, shard, context)
            is ThreadChannel -> return null
            else -> UnknownChannelUpdateEvent(channel, shard, context)

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelDelete, shard: Int, kord: Kord, context: CoroutineContext): CoreEvent? {
        cache.remove<ChannelData> { idEq(ChannelData::id, event.channel.id) }
        val data = ChannelData.from(event.channel)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel, shard, context)
            is StoreChannel -> StoreChannelDeleteEvent(channel, shard, context)
            is DmChannel -> DMChannelDeleteEvent(channel, shard, context)
            is TextChannel -> TextChannelDeleteEvent(channel, shard, context)
            is StageChannel -> StageChannelDeleteEvent(channel, shard, context)
            is VoiceChannel -> VoiceChannelDeleteEvent(channel, shard, context)
            is Category -> CategoryDeleteEvent(channel, shard, context)
            is ThreadChannel -> return null
            else -> UnknownChannelDeleteEvent(channel, shard, context)
        }

        return coreEvent
    }

    private suspend fun handle(
        event: ChannelPinsUpdate,
        shard: Int,
        kord: Kord,
        context: CoroutineContext
    ): ChannelPinsUpdateEvent =
        with(event.pins) {
            val coreEvent = ChannelPinsUpdateEvent(
                ChannelPinsUpdateEventData.from(this),
                kord,
                shard,
                coroutineContext = context
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
        context: CoroutineContext
    ): TypingStartEvent = with(event.data) {
        member.value?.let {
            cache.put(MemberData.from(userId = it.user.value!!.id, guildId = guildId.value!!, it))
        }

        return TypingStartEvent(
            TypingStartEventData.from(this),
            kord,
            shard,
            coroutineContext = context
        )
    }

}