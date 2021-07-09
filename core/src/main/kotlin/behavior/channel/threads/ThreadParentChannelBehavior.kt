package dev.kord.core.behavior.channel.threads

import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.entity.channel.thread.Thread
import kotlinx.coroutines.flow.Flow

interface ThreadParentChannelBehavior : GuildChannelBehavior {

    val publicActiveThreads: Flow<Thread> get() = supplier.getActiveThreads(id)

    val publicArchievedThreads: Flow<Thread> get() = supplier.getPublicArchivedThreads(id)


}