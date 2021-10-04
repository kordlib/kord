package dev.kord.core.event.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.cache.data.ThreadMembersUpdateEventData
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.event.Event
import kotlin.coroutines.CoroutineContext

public class ThreadMembersUpdateEvent(
    public val data: ThreadMembersUpdateEventData,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : Event {

    public val id: Snowflake get() = data.id

    public val guildId: Snowflake get() = data.guildId

    public val memberCount: Int get() = data.memberCount

    public val addedMembers: List<ThreadMember>
        get() = data.addedMembers.orEmpty().map {
            ThreadMember(it, kord)
        }

    public val removedMemberIds: List<Snowflake> get() = data.removedMemberIds.orEmpty()

    public val removedMemberBehaviors: List<MemberBehavior>
        get() = removedMemberIds.map {
            MemberBehavior(guildId, it, kord)
        }
}
