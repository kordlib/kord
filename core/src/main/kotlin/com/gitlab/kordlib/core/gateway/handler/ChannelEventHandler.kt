package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.event.channel.*
import com.gitlab.kordlib.core.toInstant
import com.gitlab.kordlib.core.toSnowflakeOrNull
import com.gitlab.kordlib.gateway.*
import kotlinx.coroutines.channels.SendChannel
import com.gitlab.kordlib.core.event.Event as CoreEvent
import kotlinx.coroutines.channels.Channel as CoroutineChannel

@Suppress("EXPERIMENTAL_API_USAGE")
internal class ChannelEventHandler(
        kord: Kord,
        gateway: Gateway,
        cache: DataCache,
        coreEventChannel: SendChannel<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreEventChannel) {

    override suspend fun handle(event: Event) = when (event) {
        is ChannelCreate -> handle(event)
        is ChannelUpdate -> handle(event)
        is ChannelDelete -> handle(event)
        is ChannelPinsUpdate -> handle(event)
        is TypingStart -> handle(event)
        else -> Unit
    }

    private suspend fun handle(event: ChannelCreate) {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val event = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel)
            is StoreChannel -> StoreChannelCreateEvent(channel)
            is DmChannel -> DMChannelCreateEvent(channel)
            is TextChannel -> TextChannelCreateEvent(channel)
            is VoiceChannel -> VoiceChannelCreateEvent(channel)
            is Category -> CategoryCreateEvent(channel)
            else -> error("unknown channel: $channel")
        }

        coreEventChannel.send(event)
    }

    private suspend fun handle(event: ChannelUpdate) {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val event = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel)
            is StoreChannel -> StoreChannelUpdateEvent(channel)
            is DmChannel -> DMChannelUpdateEvent(channel)
            is TextChannel -> TextChannelUpdateEvent(channel)
            is VoiceChannel -> VoiceChannelUpdateEvent(channel)
            is Category -> CategoryUpdateEvent(channel)
            else -> error("unknown channel: $channel")
        }

        coreEventChannel.send(event)
    }

    private suspend fun handle(event: ChannelDelete) {
        val data = ChannelData.from(event.channel)
        cache.put(data)

        val event = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel)
            is StoreChannel -> StoreChannelDeleteEvent(channel)
            is DmChannel -> DMChannelDeleteEvent(channel)
            is TextChannel -> TextChannelDeleteEvent(channel)
            is VoiceChannel -> VoiceChannelDeleteEvent(channel)
            is Category -> CategoryDeleteEvent(channel)
            else -> error("unknown channel: $channel")
        }

        coreEventChannel.send(event)
    }

    private suspend fun handle(event: ChannelPinsUpdate) = with(event.pins) {
        val event = ChannelPinsUpdateEvent(Snowflake(channelId), lastPinTimestamp?.toInstant(), kord)

        cache.find<ChannelData> { ChannelData::id eq channelId.toLong() }.update {
            it.copy(lastPinTimestamp = lastPinTimestamp ?: it.lastPinTimestamp)
        }

        coreEventChannel.send(event)
    }

    private suspend fun handle(event: TypingStart) = with(event.data) {
        val event = TypingStartEvent(
                Snowflake(channelId),
                Snowflake(userId),
                guildId.toSnowflakeOrNull(),
                timestamp.toInstant(),
                kord
        )

        coreEventChannel.send(event)
    }

}