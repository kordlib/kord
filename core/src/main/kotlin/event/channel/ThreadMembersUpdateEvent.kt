package dev.kord.core.event.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.cache.data.ThreadMembersUpdateEventData
import dev.kord.core.entity.channel.thread.ThreadUser
import dev.kord.core.event.Event

class ThreadMembersUpdateEvent(
    val data: ThreadMembersUpdateEventData,
    override val kord: Kord,
    override val shard: Int
) : Event {

    val id: Snowflake get() = data.id

    val guildId: Snowflake get() = data.guildId

    val memberCount: Int get() = data.memberCount

    val addedMembers: List<ThreadUser>
        get() = data.addedMembers.orEmpty().map {
            ThreadUser(it, kord)
        }

    val removedMemberIds: List<Snowflake> get() = data.removedMemberIds.orEmpty()

    val removedMemberBehaviors: List<MemberBehavior>
        get() = removedMemberIds.map {
            MemberBehavior(guildId, it, kord)
        }
}