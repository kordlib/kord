package dev.kord.core.gateway.handler

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

internal class ChannelEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): dev.kord.core.event.Event? = when (event) {
        is ChannelCreate -> handle(event, shard, kord, context)
        is ChannelUpdate -> handle(event, shard, kord, context)
        is ChannelDelete -> handle(event, shard, kord, context)
        is ChannelPinsUpdate -> handle(event, shard, kord, context)
        is TypingStart -> handle(event, shard, kord, context)
        else -> null
    }

    private suspend fun handle(event: ChannelCreate, shard: Int, kord: Kord, context: LazyContext?): ChannelCreateEvent? {
        val data = ChannelData.from(event.channel)
        kord.cache.put(data)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelCreateEvent(channel, shard, context?.get())
            is DmChannel -> DMChannelCreateEvent(channel, shard, context?.get())
            is TextChannel -> TextChannelCreateEvent(channel, shard, context?.get())
            is StageChannel -> StageChannelCreateEvent(channel, shard, context?.get())
            is VoiceChannel -> VoiceChannelCreateEvent(channel, shard, context?.get())
            is Category -> CategoryCreateEvent(channel, shard, context?.get())
            is ForumChannel -> ForumChannelCreateEvent(channel, shard, context?.get())
            is ThreadChannel -> return null
            else -> UnknownChannelCreateEvent(channel, shard, context?.get())

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelUpdate, shard: Int, kord: Kord, context: LazyContext?): ChannelUpdateEvent? {
        val data = ChannelData.from(event.channel)
        val oldData = kord.cache.query { idEq(ChannelData::id, data.id) }.singleOrNull()
        kord.cache.put(data)
        val old = oldData?.let { Channel.from(it, kord) }
        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelUpdateEvent(channel, old as? NewsChannel, shard, context?.get())
            is DmChannel -> DMChannelUpdateEvent(channel, old as? DmChannel, shard, context?.get())
            is TextChannel -> TextChannelUpdateEvent(channel, old as? TextChannel, shard, context?.get())
            is StageChannel -> StageChannelUpdateEvent(channel, old as? StageChannel, shard, context?.get())
            is VoiceChannel -> VoiceChannelUpdateEvent(channel, old as? VoiceChannel, shard, context?.get())
            is Category -> CategoryUpdateEvent(channel, old as? Category, shard, context?.get())
            is ForumChannel -> ForumChannelUpdateEvent(channel, old as? ForumChannel, shard, context?.get())
            is ThreadChannel -> return null
            else -> UnknownChannelUpdateEvent(channel, old, shard, context?.get())

        }

        return coreEvent
    }

    private suspend fun handle(event: ChannelDelete, shard: Int, kord: Kord, context: LazyContext?): ChannelDeleteEvent? {
        kord.cache.remove { idEq(ChannelData::id, event.channel.id) }
        val data = ChannelData.from(event.channel)

        val coreEvent = when (val channel = Channel.from(data, kord)) {
            is NewsChannel -> NewsChannelDeleteEvent(channel, shard, context?.get())
            is DmChannel -> DMChannelDeleteEvent(channel, shard, context?.get())
            is TextChannel -> TextChannelDeleteEvent(channel, shard, context?.get())
            is StageChannel -> StageChannelDeleteEvent(channel, shard, context?.get())
            is VoiceChannel -> VoiceChannelDeleteEvent(channel, shard, context?.get())
            is Category -> CategoryDeleteEvent(channel, shard, context?.get())
            is ForumChannel -> ForumChannelDeleteEvent(channel, shard, context?.get())
            is ThreadChannel -> return null
            else -> UnknownChannelDeleteEvent(channel, shard, context?.get())
        }

        return coreEvent
    }

    private suspend fun handle(
        event: ChannelPinsUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): ChannelPinsUpdateEvent =
        with(event.pins) {
            val coreEvent = ChannelPinsUpdateEvent(
                ChannelPinsUpdateEventData.from(this),
                kord,
                shard,
                context?.get(),
            )

            kord.cache.query { idEq(ChannelData::id, channelId) }.update {
                it.copy(lastPinTimestamp = lastPinTimestamp)
            }

            coreEvent
        }

    private suspend fun handle(
        event: TypingStart,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): TypingStartEvent = with(event.data) {
        member.value?.let {
            kord.cache.put(MemberData.from(userId = it.user.value!!.id, guildId = guildId.value!!, it))
        }

        TypingStartEvent(
            TypingStartEventData.from(this),
            kord,
            shard,
            context?.get(),
        )
    }

}
