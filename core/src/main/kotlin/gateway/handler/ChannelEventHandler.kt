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
import dev.kord.core.event.Event as CoreEvent

internal class ChannelEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
    ): CoreEvent? = when (event) {
        is ChannelCreate -> handle(event, shard, kord)
        is ChannelUpdate -> handle(event, shard, kord)
        is ChannelDelete -> handle(event, shard, kord)
        is ChannelPinsUpdate -> handle(event, shard, kord)
        is TypingStart -> handle(event, shard, kord)
        else -> null
    }

    private suspend fun handle(event: ChannelCreate, shard: Int, kord: Kord): ChannelCreateEvent? {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel, shard)
            is @Suppress("DEPRECATION_ERROR") StoreChannel -> @Suppress("DEPRECATION_ERROR") StoreChannelCreateEvent(channel, shard)
            is DmChannel -> DMChannelCreateEvent(channel, shard)
            is TextChannel -> TextChannelCreateEvent(channel, shard)
            is StageChannel -> StageChannelCreateEvent(channel, shard)
            is VoiceChannel -> VoiceChannelCreateEvent(channel, shard)
            is Category -> CategoryCreateEvent(channel, shard)
            is ThreadChannel -> return null
            else -> UnknownChannelCreateEvent(channel, shard)

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelUpdate, shard: Int, kord: Kord): ChannelUpdateEvent? {
        val data = ChannelData.from(event.channel)
        val oldData = cache.query<ChannelData> { idEq(ChannelData::id, data.id) }.singleOrNull()
        cache.put(data)
        val old = oldData?.let { Channel.from(it, kord) }
        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel, old as? NewsChannel, shard)
            is @Suppress("DEPRECATION_ERROR") StoreChannel -> @Suppress("DEPRECATION_ERROR") StoreChannelUpdateEvent(channel, old as? StoreChannel, shard)
            is DmChannel -> DMChannelUpdateEvent(channel, old as? DmChannel, shard)
            is TextChannel -> TextChannelUpdateEvent(channel, old as? TextChannel, shard)
            is StageChannel -> StageChannelUpdateEvent(channel, old as? StageChannel, shard)
            is VoiceChannel -> VoiceChannelUpdateEvent(channel, old as? VoiceChannel, shard)
            is Category -> CategoryUpdateEvent(channel, old as? Category, shard)
            is ThreadChannel -> return null
            else -> UnknownChannelUpdateEvent(channel, old, shard)

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelDelete, shard: Int, kord: Kord): ChannelDeleteEvent? {
        cache.remove<ChannelData> { idEq(ChannelData::id, event.channel.id) }
        val data = ChannelData.from(event.channel)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel, shard)
            is @Suppress("DEPRECATION_ERROR") StoreChannel -> @Suppress("DEPRECATION_ERROR") StoreChannelDeleteEvent(channel, shard)
            is DmChannel -> DMChannelDeleteEvent(channel, shard)
            is TextChannel -> TextChannelDeleteEvent(channel, shard)
            is StageChannel -> StageChannelDeleteEvent(channel, shard)
            is VoiceChannel -> VoiceChannelDeleteEvent(channel, shard)
            is Category -> CategoryDeleteEvent(channel, shard)
            is ThreadChannel -> return null
            else -> UnknownChannelDeleteEvent(channel, shard)
        }

        return coreEvent
    }

    private suspend fun handle(
        event: ChannelPinsUpdate,
        shard: Int,
        kord: Kord,
    ): ChannelPinsUpdateEvent =
        with(event.pins) {
            val coreEvent = ChannelPinsUpdateEvent(
                ChannelPinsUpdateEventData.from(this),
                kord,
                shard,
            )

            cache.query<ChannelData> { idEq(ChannelData::id, channelId) }.update {
                it.copy(lastPinTimestamp = lastPinTimestamp)
            }

            coreEvent
        }

    private suspend fun handle(
        event: TypingStart,
        shard: Int,
        kord: Kord,
    ): TypingStartEvent = with(event.data) {
        member.value?.let {
            cache.put(MemberData.from(userId = it.user.value!!.id, guildId = guildId.value!!, it))
        }

        TypingStartEvent(
            TypingStartEventData.from(this),
            kord,
            shard,
        )
    }

}
