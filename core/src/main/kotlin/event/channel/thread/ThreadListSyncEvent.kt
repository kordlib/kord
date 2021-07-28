package dev.kord.core.event.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.NewsChannelBehavior
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ThreadListSyncData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadUser
import dev.kord.core.event.Event


class ThreadListSyncEvent(
    val data: ThreadListSyncData,
    override val kord: Kord,
    override val shard: Int
) : Event {

    val guildId: Snowflake get() = data.guildId

    /**
     * the parent channel ids whose threads are being synced.
     * If empty, then threads were synced for the entire guild.
     */
    val channelIds: List<Snowflake> get() = data.channelIds.orEmpty()

    /**
     * Threads that are being synced for [channelIds].
     *
     * @see [channelIds]
     */
    val threads: List<ThreadChannel>
        get() = data.threads.mapNotNull {
            Channel.from(it, kord) as? ThreadChannel
        }

    /**
     * [ThreadUser] objects for the current user for each of the synced threads.
     */
    val members: List<ThreadUser> get() = data.members.map { ThreadUser(it, kord) }

}