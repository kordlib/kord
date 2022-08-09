package dev.kord.core.gateway.handler

import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.*
import dev.kord.core.event.channel.thread.*
import dev.kord.gateway.*
import dev.kord.core.event.Event as CoreEvent

public class ThreadEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(event: Event, shard: Int, kord: Kord, context: Any?): CoreEvent? = when (event) {
        is ThreadCreate -> handle(event, shard, kord, context)
        is ThreadUpdate -> handle(event, shard, kord, context)
        is ThreadDelete -> handle(event, shard, kord, context)
        is ThreadListSync -> handle(event, shard, kord, context)
        is ThreadMemberUpdate -> handle(event, shard, kord, context)
        is ThreadMembersUpdate -> handle(event, shard, kord, context)
        else -> null
    }

    public suspend fun handle(event: ThreadCreate, shard: Int, kord: Kord, context: Any?): ThreadChannelCreateEvent? {
        val channelData = event.channel.toData()
        kord.cache.put(channelData)

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadCreateEvent(channel, shard, context)
            is TextChannelThread -> TextChannelThreadCreateEvent(channel, shard, context)
            is ThreadChannel -> UnknownChannelThreadCreateEvent(channel, shard, context)
            else -> return null
        }
        return coreEvent
    }

    public suspend fun handle(event: ThreadUpdate, shard: Int, kord: Kord, context: Any?): ThreadUpdateEvent? {
        val channelData = event.channel.toData()
        val oldData = kord.cache.query<ChannelData> {
            idEq(ChannelData::id, event.channel.id)
            idEq(ChannelData::guildId, event.channel.guildId.value)
        }.singleOrNull()
        kord.cache.put(channelData)

        val old = oldData?.let { ThreadChannel(it, kord) }

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadUpdateEvent(channel, old as? NewsChannelThread, shard, context)
            is TextChannelThread -> TextChannelThreadUpdateEvent(channel, old as? TextChannelThread, shard, context)
            is ThreadChannel -> UnknownChannelThreadUpdateEvent(channel, old, shard, context)
            else -> return null
        }

        return coreEvent

    }

    public suspend fun handle(event: ThreadDelete, shard: Int, kord: Kord, context: Any?): ThreadChannelDeleteEvent {

        val channelData = event.channel.toData()
        val cachedData = kord.cache.query<ChannelData> { idEq(ChannelData::id, channelData.id) }.singleOrNull()

        val channel = DeletedThreadChannel(channelData, kord)
        val old = cachedData?.let { Channel.from(cachedData, kord) }
        val coreEvent = when (channel.type) {
            is ChannelType.PublicNewsThread -> NewsChannelThreadDeleteEvent(channel, old as? NewsChannelThread, shard, context)
            is ChannelType.PrivateThread,
            is ChannelType.GuildText -> TextChannelThreadDeleteEvent(channel, old as? TextChannelThread, shard, context)
            else -> UnknownChannelThreadDeleteEvent(channel, old as? ThreadChannel, shard, context)
        }

        kord.cache.remove<ChannelData> { idEq(ChannelData::id, channel.id) }
        return coreEvent
    }

    public suspend fun handle(event: ThreadListSync, shard: Int, kord: Kord, context: Any?): ThreadListSyncEvent {
        val data = ThreadListSyncData.from(event)

        data.threads.forEach { thread ->
            kord.cache.put(thread)
        }
        data.members.forEach { member ->
            kord.cache.put(member)
        }

        return ThreadListSyncEvent(data, kord, shard, context)
    }

    public fun handle(event: ThreadMemberUpdate, shard: Int, kord: Kord, context: Any?): ThreadMemberUpdateEvent {
        val data = ThreadMemberData.from(event.member)
        val member = ThreadMember(data, kord)
        return ThreadMemberUpdateEvent(member, kord, shard, context)
    }

    public suspend fun handle(
        event: ThreadMembersUpdate, shard: Int,
        kord: Kord,
        context: Any?,
    ): ThreadMembersUpdateEvent {
        val data = ThreadMembersUpdateEventData.from(event)
        for (removedMemberId in data.removedMemberIds.orEmpty()) {
            kord.cache.remove<ThreadMemberData> {
                idEq(ThreadMemberData::userId, removedMemberId)
                idEq(ThreadMemberData::id, data.id)
            }
        }
        for (addedMember in data.addedMembers.orEmpty()) {
            kord.cache.put(addedMember)
        }
        return ThreadMembersUpdateEvent(data, kord, shard, context)
    }
}
