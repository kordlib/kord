package dev.kord.core.behavior.channel

import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ForumChannel
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.rest.builder.channel.ForumChannelModifyBuilder
import dev.kord.rest.builder.channel.thread.StartForumThreadBuilder
import dev.kord.rest.service.patchForumChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ForumChannelBehavior : ThreadParentChannelBehavior {

    override val activeThreads: Flow<TextChannelThread>
        get() = super.activeThreads.filterIsInstance()

    override fun getPublicArchivedThreads(before: Instant?, limit: Int?): Flow<TextChannelThread> {
        return super.getPublicArchivedThreads(before, limit).filterIsInstance()
    }

    public suspend fun startPublicThread(
        name: String,
        builder: StartForumThreadBuilder.() -> Unit = {},
    ): TextChannelThread {
        return unsafeStartThread(name, builder)
    }
}

internal suspend fun ThreadParentChannelBehavior.unsafeStartThread(
    name: String,
    builder: StartForumThreadBuilder.() -> Unit,
): TextChannelThread {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.startForumThread(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannelThread
}

public suspend inline fun ForumChannelBehavior.edit(builder: ForumChannelModifyBuilder.() -> Unit): ForumChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.patchForumChannel(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ForumChannel
}
