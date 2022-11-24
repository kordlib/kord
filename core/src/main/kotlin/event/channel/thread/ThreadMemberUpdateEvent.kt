package dev.kord.core.event.channel.thread

import dev.kord.core.Kord
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.event.Event

/**
 * The event dispatched when a [ThreadMember] for the current user is updated.
 *
 * See [Thread Member Update](https://discord.com/developers/docs/topics/gateway-events#thread-member-update)
 *
 * @property member The member that triggered the event
 */
public class ThreadMemberUpdateEvent(
    public val member: ThreadMember,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event
