package dev.kord.core.event.channel.thread

import dev.kord.core.Kord
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.event.Event

class ThreadMemberUpdateEvent(
    val member: ThreadMember,
    override val kord: Kord,
    override val shard: Int
) : Event