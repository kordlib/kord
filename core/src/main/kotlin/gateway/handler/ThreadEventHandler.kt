package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
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

public class ThreadEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord): CoreEvent? = when (event) {
        is ThreadCreate -> handle(event, shard, kord)
        is ThreadUpdate -> handle(event, shard, kord)
        is ThreadDelete -> handle(event, shard, kord)
        is ThreadListSync -> handle(event, shard, kord)
        is ThreadMemberUpdate -> handle(event, shard, kord)
        is ThreadMembersUpdate -> handle(event, shard, kord)
        else -> null
    }

    public suspend fun handle(event: ThreadCreate, shard: Int, kord: Kord): ThreadChannelCreateEvent? {
        val channelData = event.channel.toData()
        cache.put(channelData)

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadCreateEvent(channel, shard)
            is TextChannelThread -> TextChannelThreadCreateEvent(channel, shard)
            is ThreadChannel -> UnknownChannelThreadCreateEvent(channel, shard)
            else -> return null
        }
        return coreEvent
    }

    public suspend fun handle(event: ThreadUpdate, shard: Int, kord: Kord): ThreadUpdateEvent? {
        val channelData = event.channel.toData()
        val oldData = cache.query<ChannelData> {
            idEq(ChannelData::id, event.channel.id)
            idEq(ChannelData::guildId, event.channel.guildId.value)
        }.singleOrNull()
        cache.put(channelData)

        val old = oldData?.let { ThreadChannel(it, kord) }

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadUpdateEvent(channel, old as? NewsChannelThread, shard)
            is TextChannelThread -> TextChannelThreadUpdateEvent(channel, old as? TextChannelThread, shard)
            is ThreadChannel -> UnknownChannelThreadUpdateEvent(channel, old, shard)
            else -> return null
        }

        return coreEvent

    }

    public suspend fun handle(event: ThreadDelete, shard: Int, kord: Kord): ThreadChannelDeleteEvent {

        val channelData = event.channel.toData()
        val cachedData = cache.query<ChannelData> { idEq(ChannelData::id, channelData.id) }.singleOrNull()

        val channel = DeletedThreadChannel(channelData, kord)
        val old = cachedData?.let { Channel.from(cachedData, kord) }
        val coreEvent = when (channel.type) {
            is ChannelType.PublicNewsThread -> NewsChannelThreadDeleteEvent(channel, old as? NewsChannelThread, shard)
            is ChannelType.PrivateThread,
            is ChannelType.GuildText -> TextChannelThreadDeleteEvent(channel, old as? TextChannelThread, shard)
            else -> UnknownChannelThreadDeleteEvent(channel, old as? ThreadChannel, shard)
        }

        cache.remove<ChannelData> { idEq(ChannelData::id, channel.id) }
        return coreEvent
    }

    public suspend fun handle(event: ThreadListSync, shard: Int, kord: Kord): ThreadListSyncEvent {
        val data = ThreadListSyncData.from(event)

        data.threads.forEach { thread ->
            cache.put(thread)
        }
        data.members.forEach { member ->
            cache.put(member)
        }

        return ThreadListSyncEvent(data, kord, shard)
    }

    public fun handle(event: ThreadMemberUpdate, shard: Int, kord: Kord): ThreadMemberUpdateEvent {
        val data = ThreadMemberData.from(event.member)
        val member = ThreadMember(data, kord)
        return ThreadMemberUpdateEvent(member, kord, shard)
    }

    public suspend fun handle(event: ThreadMembersUpdate, shard: Int, kord: Kord): ThreadMembersUpdateEvent {
        val data = ThreadMembersUpdateEventData.from(event)
        for (removedMemberId in data.removedMemberIds.orEmpty()) {
            cache.remove<ThreadMemberData> {
                idEq(ThreadMemberData::userId, removedMemberId)
                idEq(ThreadMemberData::id, data.id)
            }
        }
        for (addedMember in data.addedMembers.orEmpty()) {
            cache.put(addedMember)
        }
        return ThreadMembersUpdateEvent(data, kord, shard)
    }
}
