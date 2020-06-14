package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.channel.*
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.core.toInstant
import com.gitlab.kordlib.core.toSnowflakeOrNull
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.channels.SendChannel
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class ChannelEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

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

        val event = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel, shard)
            is StoreChannel -> StoreChannelCreateEvent(channel, shard)
            is DmChannel -> DMChannelCreateEvent(channel, shard)
            is TextChannel -> TextChannelCreateEvent(channel, shard)
            is VoiceChannel -> VoiceChannelCreateEvent(channel, shard)
            is Category -> CategoryCreateEvent(channel, shard)
            else -> error("unknown channel: $channel")
        }

        coreEventChannel.send(event)
    }

    private suspend fun handle(event: ChannelUpdate, shard: Int) {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val event = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel, shard)
            is StoreChannel -> StoreChannelUpdateEvent(channel, shard)
            is DmChannel -> DMChannelUpdateEvent(channel, shard)
            is TextChannel -> TextChannelUpdateEvent(channel, shard)
            is VoiceChannel -> VoiceChannelUpdateEvent(channel, shard)
            is Category -> CategoryUpdateEvent(channel, shard)
            else -> error("unknown channel: $channel")
        }

        coreEventChannel.send(event)
    }

    private suspend fun handle(event: ChannelDelete, shard: Int) {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val event = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel, shard)
            is StoreChannel -> StoreChannelDeleteEvent(channel, shard)
            is DmChannel -> DMChannelDeleteEvent(channel, shard)
            is TextChannel -> TextChannelDeleteEvent(channel, shard)
            is VoiceChannel -> VoiceChannelDeleteEvent(channel, shard)
            is Category -> CategoryDeleteEvent(channel, shard)
            else -> error("unknown channel: $channel")
        }

        coreEventChannel.send(event)
    }

    private suspend fun handle(event: ChannelPinsUpdate, shard: Int) = with(event.pins) {
        val event = ChannelPinsUpdateEvent(Snowflake(channelId), lastPinTimestamp?.toInstant(), kord, shard)

        cache.query<ChannelData> { ChannelData::id eq channelId.toLong() }.update {
            it.copy(lastPinTimestamp = lastPinTimestamp ?: it.lastPinTimestamp)
        }

        coreEventChannel.send(event)
    }

    private suspend fun handle(event: TypingStart, shard: Int) = with(event.data) {
        val event = TypingStartEvent(
                Snowflake(channelId),
                Snowflake(userId),
                guildId.toSnowflakeOrNull(),
                timestamp.toInstant(),
                kord,
                shard
        )

        coreEventChannel.send(event)
    }

}