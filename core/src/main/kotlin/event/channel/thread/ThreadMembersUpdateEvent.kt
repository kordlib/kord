package dev.kord.core.event.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.cache.data.ThreadMembersUpdateEventData
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.event.Event

/**
 * The event dispatched when a guild member is added or removed from a thread.
 *
 * See [Thread Members Update](https://discord.com/developers/docs/topics/gateway-events#thread-members-update)
 */
public class ThreadMembersUpdateEvent(
    public val data: ThreadMembersUpdateEventData,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {

    /**
     * The ID of the thread that triggered the event.
     */
    public val id: Snowflake get() = data.id

    /**
     * The ID of the guild the event was triggered by.
     */
    public val guildId: Snowflake get() = data.guildId

    /**
     * The approximate number of members in this thread. Capped at 50 members.
     */
    public val memberCount: Int get() = data.memberCount

    /**
     * The [List] of users added to the thread in this event.
     */
    public val addedMembers: List<ThreadMember>
        get() = data.addedMembers.orEmpty().map {
            ThreadMember(it, kord)
        }

    /**
     * A [List] IDs for the users removed from the thread in this event, or empty if null.
     */
    public val removedMemberIds: List<Snowflake> get() = data.removedMemberIds.orEmpty()

    public val removedMemberBehaviors: List<MemberBehavior>
        get() = removedMemberIds.map {
            MemberBehavior(guildId, it, kord)
        }
}
