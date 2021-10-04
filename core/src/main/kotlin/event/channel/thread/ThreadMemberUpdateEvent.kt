package dev.kord.core.event.channel.thread

import dev.kord.core.Kord
import dev.kord.core.entity.channel.thread.ThreadMember
import dev.kord.core.event.Event
import kotlin.coroutines.CoroutineContext

public class ThreadMemberUpdateEvent(
    public val member: ThreadMember,
    override val kord: Kord,
    override val shard: Int,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : Event
