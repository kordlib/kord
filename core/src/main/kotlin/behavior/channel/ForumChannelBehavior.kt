package dev.kord.core.behavior.channel

import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ForumChannel
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.rest.builder.channel.ForumChannelModifyBuilder
import dev.kord.rest.builder.channel.thread.StartForumThreadBuilder
import dev.kord.rest.service.patchForumChannel
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ForumChannelBehavior : ThreadParentChannelBehavior {
    public suspend fun startPublicThread(
        name: String,
        builder: StartForumThreadBuilder.() -> Unit = {}
    ): TextChannelThread {
        return unsafeStartThread(name, builder) as TextChannelThread
    }
}

internal suspend fun ThreadParentChannelBehavior.unsafeStartThread(
    name: String,
    builder: StartForumThreadBuilder.() -> Unit
): ThreadChannel {
    val response = kord.rest.channel.startForumThread(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ThreadChannel
}

public suspend inline fun ForumChannelBehavior.edit(builder: ForumChannelModifyBuilder.() -> Unit): ForumChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchForumChannel(id, builder)
    val data = ChannelData.from(response)
    return Channel.from(data, kord) as ForumChannel
}
