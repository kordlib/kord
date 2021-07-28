package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.remove
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.cache.data.ThreadListSyncData
import dev.kord.core.cache.data.ThreadMembersUpdateEventData
import dev.kord.core.cache.data.ThreadUserData
import dev.kord.core.cache.data.toData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadUser
import dev.kord.core.event.channel.*
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.*
import kotlinx.coroutines.flow.MutableSharedFlow

class ThreadEventHandler(
    kord: Kord,
    gateway: MasterGateway,
    cache: DataCache,
    coreFlow: MutableSharedFlow<dev.kord.core.event.Event>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {
    override suspend fun handle(event: Event, shard: Int) {
        when (event) {
            is ThreadCreate -> handle(event, shard)
            is ThreadDelete -> handle(event, shard)
            is ThreadListSync -> handle(event, shard)
            is ThreadMemberUpdate -> handle(event, shard)
            is ThreadMembersUpdate -> handle(event, shard)
            else -> Unit

        }
    }

    suspend fun handle(event: ThreadCreate, shard: Int) {
        val channelData = event.channel.toData()
        cache.put(channelData)

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadCreateEvent(channel, shard)
            is TextChannelThread -> TextChannelThreadCreateEvent(channel, shard)
        }
    }

    suspend fun handle(event: ThreadUpdate, shard: Int) {
        val channelData = event.channel.toData()
        cache.put(channelData)

        val coreEvent = when (val channel = Channel.from(channelData, kord)) {
            is NewsChannelThread -> NewsChannelThreadUpdateEvent(channel, shard)
            is TextChannelThread -> TextChannelThreadUpdateEvent(channel, shard)
        }
    }

    suspend fun handle(event: ThreadListSync, shard: Int) {
        val data = ThreadListSyncData.from(event)
        for (thread in data.threads) {
            cache.put(thread)
        }
        for (member in data.members) {
            cache.put(member)
        }
        val coreEvent = ThreadListSyncEvent(data, kord, shard)

        coreFlow.emit(coreEvent)
    }

    suspend fun handle(event: ThreadMemberUpdate, shard: Int) {
        val data = ThreadUserData.from(event.member)
        val member = ThreadUser(data, kord)
        val coreEvent = ThreadMemberUpdateEvent(member, kord, shard)
        coreFlow.emit(coreEvent)
    }

    suspend fun handle(event: ThreadMembersUpdate, shard: Int) {
        val data = ThreadMembersUpdateEventData.from(event)
        for (removedMemberId in data.removedMemberIds.orEmpty()) {
            cache.remove<ThreadUserData> {
                idEq(ThreadUserData::userId, removedMemberId)
                idEq(ThreadUserData::id, data.id)
            }
        }
        for(addedMember in data.addedMembers.orEmpty()) {
            cache.put(addedMember)
        }
        val coreEvent = ThreadMembersUpdateEvent(data, kord, shard)
        coreFlow.emit(coreEvent)
    }
}