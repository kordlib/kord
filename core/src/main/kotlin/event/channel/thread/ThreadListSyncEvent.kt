package dev.kord.core.event.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ThreadListSyncData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadUser
import dev.kord.core.event.Event


class ThreadListSyncEvent(
    val data: ThreadListSyncData,
    override val kord: Kord,
    override val shard: Int
) : Event {

    val guildId: Snowflake get() = data.guildId

    val channelIds: List<Snowflake>? get() = data.channelIds.value

    val channelBehaviors: List<ThreadParentChannelBehavior>
        get() = channelIds.orEmpty().map {
            ThreadParentChannelBehavior(guildId, it, kord)
        }

    val threads: List<ThreadChannel> get() = data.threads.mapNotNull { Channel.from(it, kord) as? ThreadChannel }

    val members: List<ThreadUser> get() = data.members.map { ThreadUser(it, kord) }

}